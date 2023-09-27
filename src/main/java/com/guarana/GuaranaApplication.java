package com.guarana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GuaranaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuaranaApplication.class, args);
    }
}

