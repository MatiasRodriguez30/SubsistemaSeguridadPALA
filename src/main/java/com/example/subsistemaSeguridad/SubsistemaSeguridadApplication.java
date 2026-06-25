package com.example.subsistemaSeguridad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SubsistemaSeguridadApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubsistemaSeguridadApplication.class, args);
	}

}
