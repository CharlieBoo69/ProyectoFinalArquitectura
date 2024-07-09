package com.example.APIGateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRoutrLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("events_service", r-> r.path("/api/events/**")
                        .uri("http://localhost:8081"))
                .route("participants_service", r-> r.path("/api/participants/**")
                        .uri("http://localhost:8082"))
                .route("notification_service", r-> r.path("/api/notifications/**")
                        .uri("http://localhost:8083"))
                .build();
    }

}