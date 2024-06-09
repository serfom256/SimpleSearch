package com.simplesearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SimpleSearchApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SimpleSearchApplication.class, args);
    }

}
