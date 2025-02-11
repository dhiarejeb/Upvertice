package com.dhia.Upvertise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class UpvertiseApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpvertiseApiApplication.class, args);
	}

}
