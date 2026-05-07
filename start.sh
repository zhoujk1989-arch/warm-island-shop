#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"
LOG_DIR="$ROOT_DIR/logs"

BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"
DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-warm_island}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-root}"

BACKEND_PID=""
FRONTEND_PID=""

mkdir -p "$LOG_DIR"

log() {
  printf '[warm-island] %s\n' "$1"
}

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

port_in_use() {
  local port="$1"

  if command_exists lsof; then
    lsof -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1
  else
    curl -fsS "http://127.0.0.1:$port" >/dev/null 2>&1
  fi
}

kill_port() {
  local port="$1"
  local name="$2"
  local pids

  if ! command_exists lsof; then
    log "缺少 lsof，无法自动清理 $name 端口 $port"
    return 0
  fi

  pids="$(lsof -tiTCP:"$port" -sTCP:LISTEN || true)"

  if [[ -z "$pids" ]]; then
    return 0
  fi

  log "$name 端口 $port 已被占用，正在停止进程：$(echo "$pids" | tr '\n' ' ')"
  kill $pids >/dev/null 2>&1 || true
  sleep 1

  if port_in_use "$port"; then
    log "$name 端口 $port 仍被占用，正在强制停止"
    kill -9 $pids >/dev/null 2>&1 || true
    sleep 1
  fi

  if port_in_use "$port"; then
    log "$name 端口 $port 清理失败，请手动检查"
    exit 1
  fi
}

cleanup() {
  log "正在停止服务..."

  if [[ -n "$FRONTEND_PID" ]] && kill -0 "$FRONTEND_PID" >/dev/null 2>&1; then
    kill "$FRONTEND_PID" >/dev/null 2>&1 || true
  fi

  if [[ -n "$BACKEND_PID" ]] && kill -0 "$BACKEND_PID" >/dev/null 2>&1; then
    kill "$BACKEND_PID" >/dev/null 2>&1 || true
  fi
}

wait_for_url() {
  local url="$1"
  local name="$2"
  local attempts=60

  for _ in $(seq 1 "$attempts"); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      log "$name 已就绪"
      return 0
    fi
    sleep 1
  done

  log "$name 启动超时，请查看日志"
  return 1
}

require_command() {
  local name="$1"

  if ! command_exists "$name"; then
    log "缺少命令：$name"
    exit 1
  fi
}

trap cleanup EXIT INT TERM

require_command mvn
require_command npm
require_command curl

kill_port "$BACKEND_PORT" "后端"
kill_port "$FRONTEND_PORT" "前端"

if [[ ! -d "$FRONTEND_DIR/node_modules" ]]; then
  log "前端依赖不存在，正在执行 npm install..."
  (cd "$FRONTEND_DIR" && npm install)
fi

log "启动后端：http://127.0.0.1:$BACKEND_PORT"
(
  cd "$BACKEND_DIR"
  env \
    PORT="$BACKEND_PORT" \
    DB_HOST="$DB_HOST" \
    DB_PORT="$DB_PORT" \
    DB_NAME="$DB_NAME" \
    DB_USER="$DB_USER" \
    DB_PASSWORD="$DB_PASSWORD" \
    mvn spring-boot:run
) >"$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID="$!"

wait_for_url "http://127.0.0.1:$BACKEND_PORT/api/products?pageNum=1&pageSize=1" "后端"

log "启动前端：http://127.0.0.1:$FRONTEND_PORT/admin"
(
  cd "$FRONTEND_DIR"
  npm run dev -- --host  --port "$FRONTEND_PORT" --strictPort
) >"$LOG_DIR/frontend.log" 2>&1 &
FRONTEND_PID="$!"

wait_for_url "http://127.0.0.1:$FRONTEND_PORT/admin" "前端"

log "启动完成"
log "前台商城：http://127.0.0.1:$FRONTEND_PORT/"
log "后台管理：http://127.0.0.1:$FRONTEND_PORT/admin"
log "后端接口：http://127.0.0.1:$BACKEND_PORT/api/products"
log "日志目录：$LOG_DIR"
log "按 Ctrl+C 停止前后端服务"

wait "$BACKEND_PID" "$FRONTEND_PID"
