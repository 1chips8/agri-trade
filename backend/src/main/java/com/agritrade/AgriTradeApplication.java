package com.agritrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.agritrade")
@EnableScheduling
@SpringBootApplication
public class AgriTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriTradeApplication.class, args);
    }
}
