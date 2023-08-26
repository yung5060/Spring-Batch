package com.kbank.eai.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SkipConfigJob {

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
				.<String, String>chunk(5)
				.reader(new ItemReader<String>() {
					int i = 0;

					@Override
					public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
						i++;
						if (i == 3) {
							throw new SkippableException("skip");
						}
						System.out.println("ItemReader : " + i);
						return i > 20 ? null : String.valueOf(i);
					}
				})
				.processor(itemProcessor())
				.writer(itemWriter())
				.faultTolerant()
				.skip(SkippableException.class)
				.skipLimit(2)
				.build();
	}

	@Bean
	public ItemProcessor<? super String, String> itemProcessor() {
		return new SkipItemProcessor();
	}

	@Bean
	public ItemWriter<? super String> itemWriter() {
		return new SkipItemWriter();
	}
}
