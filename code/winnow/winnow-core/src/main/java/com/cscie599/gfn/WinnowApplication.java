package com.cscie599.gfn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WinnowApplication {
	public static void main(String[] args) {
		SpringApplication.run(WinnowApplication.class, args);
	}
}
