package com.realtimefxrate.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Enable scheduling for periodic rate generation
public class FxrateApplication {

	public static void main(String[] args) {
		SpringApplication.run(FxrateApplication.class, args);
	}

}
