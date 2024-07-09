package com.example.MicroGestionEventos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MicroGestionEventosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroGestionEventosApplication.class, args);
	}

}
