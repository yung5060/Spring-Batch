package com.kbank.eai.listener;

import java.util.HashMap;
import java.util.List;

import org.springframework.batch.core.ItemWriteListener;

public class CustomItemWriteListener implements ItemWriteListener<HashMap> {

	@Override
	public void beforeWrite(List<? extends HashMap> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterWrite(List<? extends HashMap> items) {
		// TODO Auto-generated method stub
		System.out.println("Thread : " + Thread.currentThread().getName() + " write items : " + items.size());
	}

	@Override
	public void onWriteError(Exception exception, List<? extends HashMap> items) {
		// TODO Auto-generated method stub
		
	}

	
}
