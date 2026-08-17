package com.kevinruedasv.mypersonalscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MypersonalschedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MypersonalschedulerApplication.class, args);
	}

}
