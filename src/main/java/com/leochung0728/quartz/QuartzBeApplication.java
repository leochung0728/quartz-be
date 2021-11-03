package com.leochung0728.quartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class QuartzBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuartzBeApplication.class, args);
	}
}
