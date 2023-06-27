package com.kbank.eai;

import com.kbank.eai.encryptor.EaiBatchEncryptor;

public class EaiBatchEncryptorApplication {

	public static void main(String[] args) {
		
		EaiBatchEncryptor encryptor = new EaiBatchEncryptor(args[0]);
		if(args[1].equals("enc")) {
			System.out.println(encryptor.encrypt(args[2]));
		} else {
			System.out.println(encryptor.decrypt(args[2]));
		}
	}
}
