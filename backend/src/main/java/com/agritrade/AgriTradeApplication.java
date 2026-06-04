package com.agritrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.agritrade")
@SpringBootApplication
public class AgriTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriTradeApplication.class, args);
    }
}
