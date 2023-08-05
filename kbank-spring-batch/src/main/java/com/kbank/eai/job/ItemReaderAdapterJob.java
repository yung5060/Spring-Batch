package com.kbank.eai.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbank.eai.entity.Customer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ItemReaderAdapterJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job batchJob() {
		return jobBuilderFactory.get("batchJob")
				.incrementer(new RunIdIncrementer())
				.start(step1())
				.build();
	}
	
//	@Bean
//	public Step step1() {
//		return stepBuilderFactory.get("step1")
//				.<Customer, Customer>chunk(3)
//				.
//	}
}
