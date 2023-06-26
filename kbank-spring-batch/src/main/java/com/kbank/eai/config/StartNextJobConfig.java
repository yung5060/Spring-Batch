package com.kbank.eai.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class StartNextJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job batchJob() {
		return jobBuilderFactory.get("batchJob")
				.start(flowA())
				.next(step3())
				.next(flowB())
				.next(step6())
				.end()
				.build();
	}
	
	@Bean
	public Flow flowA() {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowA");
		return flowBuilder.start(step1())
				.next(step2())
				.end();
	}
	
	@Bean
	public Flow flowB() {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowB");
		return flowBuilder.start(step4())
				.next(step5())
				.end();
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("step1 has executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("step2 has executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("step3 has executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Step step4() {
		return stepBuilderFactory.get("step4")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("step4 has executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Step step5() {
		return stepBuilderFactory.get("step5")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("step5 has executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Step step6() {
		return stepBuilderFactory.get("step6")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("step1 has executed");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
}
