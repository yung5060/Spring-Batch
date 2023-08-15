package com.kbank.eai.job.tutorial.itemWriterAdapter;

public class CustomWriterService<T> {
	
	public void customWrite(T item) {
		System.out.println(item);
	}
	
}
