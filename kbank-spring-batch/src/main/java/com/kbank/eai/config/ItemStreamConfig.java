package com.kbank.eai.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbank.eai.util.CustomItemStreamReader;
import com.kbank.eai.util.CustomItemStreamWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ItemStreamConfig {
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("batchJob")
				.start(step1())
				.next(step2())
				.build();
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<String, String>chunk(2)
				.reader(itemReader())
				.writer(itemWriter())
				.build();
	}
	
	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(">> step2 has been executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(">> step3 has been executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	public CustomItemStreamReader itemReader() {
		
		List<String> items = new ArrayList<>(10);
		
		for (int i = 1; i <= 10; i++) {
			items.add(String.valueOf(i));
		}
		
		return new CustomItemStreamReader(items);
	}
	
	@Bean
	public ItemWriter<? super String> itemWriter() {
		
		return new CustomItemStreamWriter();
	}

}
