package com.kbank.eai.config.tutorial;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
public class ChunkConfig {

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
				.reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6", "item7")))
				.processor(new ItemProcessor<String, String>() {
					@Override
					@Nullable
					public String process(@NonNull String item) throws Exception {
						System.out.println("item: " + item);
						return "my_" + item;
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
