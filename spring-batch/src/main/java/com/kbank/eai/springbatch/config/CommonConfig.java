package com.kbank.eai.springbatch.config;

import org.jasypt.encryption.pbe.PBEStringCleanablePasswordEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;



@Configuration
public class CommonConfig {

	@Value("${key}")
	private String key;
	
	@Primary
	@Bean(name="encryptorBean")
	public PBEStringCleanablePasswordEncryptor encryptorBean() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		encryptor.setPassword(key);
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setStringOutputType("base64");
		encryptor.setPoolSize(1);
		return encryptor;
	}
}
