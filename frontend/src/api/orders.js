import request from './request'

export function createOrder(orderData) {
  return request({
    url: '/api/orders',
    method: 'post',
    data: orderData,
  })
}

export function fetchOrders() {
  return request({
    url: '/api/orders',
    method: 'get',
  })
}

export function fetchOrderById(id) {
  return request({
    url: `/api/orders/${id}`,
    method: 'get',
  })
}
