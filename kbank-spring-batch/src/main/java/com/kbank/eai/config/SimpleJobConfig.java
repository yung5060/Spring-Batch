package com.kbank.eai.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SimpleJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job simpleJob() {
		return jobBuilderFactory.get("simpleJob")
				.start(simpleStep1())
				.next(simpleStep2())
				.incrementer(new RunIdIncrementer())
				.build();
	}

	@Bean
	public Step simpleStep1() {
		return stepBuilderFactory.get("simpleStep1")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("simple job 1 executed!");
					return RepeatStatus.FINISHED;
				})
				.build();
	}

	@Bean
	public Step simpleStep2() {
		return stepBuilderFactory.get("simpleStep2")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("simple job 2 executed!");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
}
