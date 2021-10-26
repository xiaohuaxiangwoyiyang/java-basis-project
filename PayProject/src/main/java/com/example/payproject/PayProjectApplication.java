package com.example.payproject;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Slf4j
@MapperScan("com.example.payproject.dao")
public class PayProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayProjectApplication.class, args);
    }

}
