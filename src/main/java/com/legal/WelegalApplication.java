package com.legal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WelegalApplication {

	public static void main(String[] args) {
         SpringApplication.run(WelegalApplication.class, args);
	}

}