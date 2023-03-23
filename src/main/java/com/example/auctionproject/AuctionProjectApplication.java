package com.example.auctionproject;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class AuctionProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionProjectApplication.class, args);
    }

}
