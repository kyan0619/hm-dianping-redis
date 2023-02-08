package com.yuan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.yuan.mapper")
@SpringBootApplication
public class RedisReview {

    public static void main(String[] args) {
        SpringApplication.run(RedisReview.class, args);
    }

}
