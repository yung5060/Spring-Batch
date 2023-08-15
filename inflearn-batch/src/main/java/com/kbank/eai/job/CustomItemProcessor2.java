package com.kbank.eai.job;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CustomItemProcessor2 implements ItemProcessor<ProcessorInfo, ProcessorInfo>{

	@Override
	@Nullable
	public ProcessorInfo process(@NonNull ProcessorInfo item) throws Exception {
		
		System.out.println("CustomItemProcessor2");
		
		return item;
	}

}
