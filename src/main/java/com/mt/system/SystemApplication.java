package com.mt.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SystemApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext =
            SpringApplication.run(SystemApplication.class, args);
        // ApplicationContextHolder.setApplicationContext(configurableApplicationContext);
    }

}