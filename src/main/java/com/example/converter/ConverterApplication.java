package com.example.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConverterApplication {

    public static void main(String[] args) {

        // Disable web environment for CLI application
        System.setProperty("spring.main.web-application-type", "none");
        System.setProperty("spring.main.banner-mode", "off");
        SpringApplication.run(ConverterApplication.class, args);
    }

}
