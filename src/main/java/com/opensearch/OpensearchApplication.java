package com.opensearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OpensearchApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(OpensearchApplication.class, args);
    }

}
