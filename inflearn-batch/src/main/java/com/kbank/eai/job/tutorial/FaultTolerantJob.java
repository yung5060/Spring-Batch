package com.kbank.eai.job.tutorial;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class FaultTolerantJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job batchJob() {
		return jobBuilderFactory.get("batchJob")
				.incrementer(new RunIdIncrementer())
				.start(step1())
				.build();
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<String, String>chunk(2)
				.reader(new ItemReader<String>() {
					
					int i = 0;
					
					@Override
					public String read()
							throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
						
						i++;
						if (i == 1) {
							throw new IllegalArgumentException("this exception is skipped");
						}
						
						return i > 3 ? null : "item" + i;
					}
					
				})
				.processor(new ItemProcessor<String, String>() {
					@Override
					public String process(String item) throws Exception {
						
						throw new IllegalStateException("this exception is retried");
						
//						return null;
					}
				})
				.writer(items -> System.out.println(items))
				.faultTolerant()
				.skip(IllegalArgumentException.class)
				.skipLimit(2)
				.retry(IllegalStateException.class)
				.retryLimit(2)
				.build();
	}
}
