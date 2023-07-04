package com.kbank.eai.config.tutorial;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class FlowStepConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("batchJob")
				.start(flowStep())
				.next(step2())
				.build();
	}
	
	private Step flowStep() { 
		return stepBuilderFactory.get("flowStep")
				.flow(flow())
				.build();
	}
	
	private Flow flow() {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");
		flowBuilder.start(step1())
					.end();
		return flowBuilder.build();
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(">> step1 has been executed");
//					throw new RuntimeException("step1 has failed");
					return RepeatStatus.FINISHED;
				})
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
}
