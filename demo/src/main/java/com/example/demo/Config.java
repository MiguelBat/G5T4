package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    private int userId = -1; // Default value indicating user not logged in

    @Bean
    public int userId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
