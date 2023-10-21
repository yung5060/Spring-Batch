package com.kbank.eai.util.encryptor;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PBEStringCleanablePasswordEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;

public class EaiBatchEncryptor implements StringEncryptor {

	private final String prefix = "ENC(";
	private final String suffix = ")";
	private final PBEStringCleanablePasswordEncryptor delegate;
	
	public EaiBatchEncryptor(String key) {
		this.delegate = delegateObject(key);
	}
	
	@Override
	public String encrypt(String message) {
		return prefix + delegate.encrypt(message) + suffix;
	}
	
	@Override
	public String decrypt(String encryptedMessage) {
		if (encryptedMessage.isEmpty()) {
			return "";
		} else if (encryptedMessage.startsWith(prefix) && encryptedMessage.endsWith(suffix)) {
			return delegate.decrypt(encryptedMessage.substring(4, encryptedMessage.length() - 1));
		} else {
			throw new IllegalArgumentException("Format not supported.");
		}
	}
	
	private PBEStringCleanablePasswordEncryptor delegateObject(String key) {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		encryptor.setPassword(key);
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setStringOutputType("base64");
		encryptor.setPoolSize(1);
		return encryptor;
	}
	
}
