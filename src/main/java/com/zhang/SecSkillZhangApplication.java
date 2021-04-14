package com.zhang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.sql.Timestamp;

@EnableAsync
@SpringBootApplication
public class SecSkillZhangApplication {

    public static void main(String[] args) {

        SpringApplication.run(SecSkillZhangApplication.class, args);
    }

}
