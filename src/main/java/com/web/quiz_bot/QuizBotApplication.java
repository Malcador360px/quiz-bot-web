package com.web.quiz_bot;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuizBotApplication {

	public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
		SpringApplication.run(QuizBotApplication.class, args);
	}

}
