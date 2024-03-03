package com.kachinga.hr;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import java.util.TimeZone;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.kachinga.hr.repository")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));
    }
}
