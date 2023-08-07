package com.kbank.eai.job.tutorial.itemReaderAdapter;

public class CustomService<T> {
	
	private int cnt =0;
	
	public T customRead() {
		return (T)("item" + cnt++);
	}
	
}
