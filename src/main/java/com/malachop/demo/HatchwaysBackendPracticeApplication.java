package com.malachop.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zalando.logbook.Logbook;

@SpringBootApplication
public class HatchwaysBackendPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HatchwaysBackendPracticeApplication.class, args);
	}
	
	@Bean
	public Logbook logbook() {
		return Logbook.create();
	}

}
