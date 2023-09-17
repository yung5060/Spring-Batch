package com.kbank.eai.listener;

import java.util.HashMap;

import org.springframework.batch.core.ItemReadListener;

public class CustomItemReadListener implements ItemReadListener<HashMap>{

	@Override
	public void beforeRead() {
		
	}

	@Override
	public void afterRead(HashMap item) {
		System.out.println("Thread : " + Thread.currentThread().getName() + " read item : " + item.toString());
	}

	@Override
	public void onReadError(Exception ex) {
		
	}
	
}
