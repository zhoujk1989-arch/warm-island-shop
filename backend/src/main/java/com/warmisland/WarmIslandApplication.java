package com.warmisland;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.warmisland.mapper")
public class WarmIslandApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarmIslandApplication.class, args);
    }

}
