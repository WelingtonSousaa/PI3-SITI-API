package com.siti.sitiapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SitiApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SitiApiApplication.class, args);
    }

}
