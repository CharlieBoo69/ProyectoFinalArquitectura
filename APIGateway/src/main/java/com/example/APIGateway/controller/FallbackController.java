package com.example.APIGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @GetMapping("/fallback")
    public String fallback() {
        return "This service is currently unavailable. Please try again later.";
    }
}
