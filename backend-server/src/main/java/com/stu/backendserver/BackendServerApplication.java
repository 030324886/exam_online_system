package com.stu.backendserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.stu.backendserver.mapper")
public class BackendServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendServerApplication.class, args);
    }
}