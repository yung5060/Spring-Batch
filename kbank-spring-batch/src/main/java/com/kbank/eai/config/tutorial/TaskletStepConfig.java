package com.kbank.eai.config.tutorial;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class TaskletStepConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job taskJob() {
		return this.jobBuilderFactory.get("taskJob")
				.start(chunkStep())
				.incrementer(new RunIdIncrementer())
				.build();
	}
	
	@Bean
	public Step taskStep() {
		return this.stepBuilderFactory.get("taskStep")
				.tasklet(new Tasklet() {
					
					@Override
					@Nullable
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
						System.out.println("step was executed");
						return RepeatStatus.FINISHED;
					}
				})
				.build();
	}
	
	@Bean
	public Step chunkStep() {
		return this.stepBuilderFactory.get("chunkStep")
				.<String, String>chunk(10)	// <input, output>
				.reader(new ListItemReader<>(Arrays.asList("a|b|c|d|e|f|g".split("\\|"))))
				.processor(new ItemProcessor<String, String>() {
					@Override
					@Nullable
					public String process(@NonNull String item) throws Exception {
						return item.toUpperCase();
					}
				})
				.writer(new ItemWriter<String>() {
					@Override
					public void write(List<? extends String> items) throws Exception {
						items.forEach(item -> System.out.println(item));
					}
				})
				.build();
	}
}
