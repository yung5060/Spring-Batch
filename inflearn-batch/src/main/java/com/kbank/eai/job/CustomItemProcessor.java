package com.kbank.eai.job;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CustomItemProcessor implements ItemProcessor<ProcessorInfo, ProcessorInfo>{

	@Override
	@Nullable
	public ProcessorInfo process(@NonNull ProcessorInfo item) throws Exception {
		
		System.out.println("CustomItemProcessor1");
		
		return item;
	}

}
