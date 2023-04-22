package com.kbank.eai.springbatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DefaultJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job defaultJob() {
		return jobBuilderFactory.get("defaultJob")
				.start(defaultStep1())
				.next(defaultStep2())
				.next(defaultStep3())
				.build();
	}
	
	@Bean
	public Step defaultStep1() {
		return stepBuilderFactory.get("defaultStep1")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(">> step1 has been executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Step defaultStep2() {
		return stepBuilderFactory.get("defaultStep2")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(">> step2 has been executed");
//					throw new RuntimeException("step2 has failed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Step defaultStep3() {
		return stepBuilderFactory.get("defaultStep3")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(">> step3 has been executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
}
