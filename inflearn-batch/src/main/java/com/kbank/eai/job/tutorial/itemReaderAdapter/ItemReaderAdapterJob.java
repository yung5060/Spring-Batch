package com.kbank.eai.job.tutorial.itemReaderAdapter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class ItemReaderAdapterJob {

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
				.<String, String>chunk(3)
				.reader(customItemReader())
				.writer(customItemWriter())
				.build();
	}
	
	@Bean
	public ItemReader<String> customItemReader() {
		
		ItemReaderAdapter<String> reader = new ItemReaderAdapter<>();
		reader.setTargetObject(customService());
		reader.setTargetMethod("customRead");
		
		return reader;
	}
	
	@Bean
	public Object customService() {
		return new CustomService<String>();
	}

	@Bean
	public ItemWriter<String> customItemWriter() {
		return items -> {
			System.out.println(items);
		};
	}
}
