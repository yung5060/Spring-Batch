package com.kbank.eai.job.tutorial.compositeItemProcessor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CustomItemProcessor implements ItemProcessor<String, String>{

	int cnt = 0;

	@Override
	@Nullable
	public String process(@NonNull String item) throws Exception {
		cnt++;
		return item + cnt;
	}
	
	
}
