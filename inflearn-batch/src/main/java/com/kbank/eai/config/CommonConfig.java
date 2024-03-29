package com.kbank.eai.config;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbank.eai.util.encryptor.EaiBatchEncryptor;


@Configuration
public class CommonConfig {

	@Value("${key}")
	private String key;
	
	@Bean
	public StringEncryptor encryptorBean() {
		return new EaiBatchEncryptor(key);
	}
	
}
