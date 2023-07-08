package com.kbank.eai.config.tutorial;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

import com.kbank.eai.entity.Customer;
import com.kbank.eai.util.CustomItemProcessor;
import com.kbank.eai.util.CustomItemReader;
import com.kbank.eai.util.CustomItemWriter;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class ItemReadProcessWriteConfig {

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
				.<Customer, Customer>chunk(3)
				.reader(itemReader())
				.processor(itemProcessor())
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

	@Bean
	public CustomItemReader itemReader() {
		return new CustomItemReader(Arrays.asList(new Customer("user1")
				, new Customer("user2")
				, new Customer("user3")
				, new Customer("user4")
				, new Customer("user5")
				, new Customer("user6")
				, new Customer("user7")
		));
	}

	@Bean
	public CustomItemProcessor itemProcessor() {
		return new CustomItemProcessor();
	}

	@Bean
	public CustomItemWriter itemWriter() {
		return new CustomItemWriter();
	}
}
