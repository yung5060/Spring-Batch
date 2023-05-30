package com.kbank.eai;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class KbankSpringBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(KbankSpringBatchApplication.class, args);
	}

}
