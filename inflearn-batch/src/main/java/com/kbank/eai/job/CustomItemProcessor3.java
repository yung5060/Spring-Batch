package com.kbank.eai.job;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CustomItemProcessor3 implements ItemProcessor<ProcessorInfo, ProcessorInfo>{

	@Override
	@Nullable
	public ProcessorInfo process(@NonNull ProcessorInfo item) throws Exception {
		
		System.out.println("CustomItemProcessor3");
		
		return item;
	}

}
