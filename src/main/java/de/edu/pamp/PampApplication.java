package de.edu.pamp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class PampApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PampApplication.class, args);
	}
}