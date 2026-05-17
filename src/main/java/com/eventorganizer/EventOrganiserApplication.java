package com.eventorganizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EventOrganiserApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventOrganiserApplication.class, args);
	}

}
