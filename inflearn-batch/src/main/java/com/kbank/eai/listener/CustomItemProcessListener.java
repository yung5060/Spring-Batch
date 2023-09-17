package com.kbank.eai.listener;

import java.util.HashMap;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.lang.Nullable;

public class CustomItemProcessListener implements ItemProcessListener<HashMap, HashMap>{

	@Override
	public void beforeProcess(HashMap item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterProcess(HashMap item, @Nullable HashMap result) {
		// TODO Auto-generated method stub
		System.out.println("Thread : " + Thread.currentThread().getName() + " process item : " + item.toString());
	}

	@Override
	public void onProcessError(HashMap item, Exception e) {
		// TODO Auto-generated method stub
		
	}

	
}
