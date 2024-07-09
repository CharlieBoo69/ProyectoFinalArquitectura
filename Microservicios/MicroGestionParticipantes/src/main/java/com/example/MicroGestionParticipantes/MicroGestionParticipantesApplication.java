package com.example.MicroGestionParticipantes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MicroGestionParticipantesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroGestionParticipantesApplication.class, args);
	}

}
