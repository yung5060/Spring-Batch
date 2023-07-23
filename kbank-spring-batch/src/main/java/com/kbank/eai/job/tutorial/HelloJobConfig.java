package com.kbank.eai.job.tutorial;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class HelloJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job parentJob() {
		return this.jobBuilderFactory.get("parentJob").start(jobStep(null)).next(step2()).build();
	}

	@Bean
	public Step jobStep(JobLauncher jobLauncher) {
		return this.stepBuilderFactory.get("jobStep").job(childJob()).launcher(jobLauncher)
				.parametersExtractor(jobParametersExtractor()).listener(new StepExecutionListener() {

					@Override
					public void beforeStep(StepExecution stepExecution) {
						stepExecution.getExecutionContext().putString("name", "user1");
					}

					@Override
					@Nullable
					public ExitStatus afterStep(StepExecution stepExecution) {
						return null;
					}
				}).build();
	}

	private DefaultJobParametersExtractor jobParametersExtractor() {
		DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
		extractor.setKeys(new String[] { "name" });
		return extractor;
	}

	@Bean
	public Job childJob() {
		return this.jobBuilderFactory.get("childJob").start(step1()).build();
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
//				.tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
				.tasklet(new Tasklet() {

					@Override
					@Nullable
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
							throws Exception {
//						throw new RuntimeException("step1 has failed");
						return RepeatStatus.FINISHED;
					}
				}).build();
	}

	@Bean
	public Step step2() {
		return this.stepBuilderFactory.get("step2")
//				.tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
				.tasklet(new Tasklet() {

					@Override
					@Nullable
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
							throws Exception {
						throw new RuntimeException("step2 has failed");
//						return RepeatStatus.FINISHED;
					}
				}).build();
	}
}
