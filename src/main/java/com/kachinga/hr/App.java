package com.kachinga.hr;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.TimeZone;

@SpringBootApplication
@EnableDiscoveryClient
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        startSuccess();
    }

    private static void startSuccess() {
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_BOLD = "\u001B[1m";
        final String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_GREEN + ANSI_BOLD + "Human Resource Service Started Successfully!" + ANSI_RESET);
    }

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));
    }
}
