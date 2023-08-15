package com.kbank.eai.job.tutorial.itemWriterAdapter;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class ItemWriterAdapterJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final ChunkListener customChunkListener;
	
	private final int chunkSize = 3;
	
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
				.<String, String>chunk(chunkSize)
				.reader(new ItemReader<String> () {
					int i = 0;
					@Override
					@Nullable
					public String read()
							throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
						i++;
						return i > 10 ? null : "item" + i;
					}
				})
				.writer(customItemWriter())
				.listener(customChunkListener)
				.build();
	}
	
	@Bean
	public ItemWriter<? super String> customItemWriter() {
		ItemWriterAdapter<String> writer = new ItemWriterAdapter<>();
		writer.setTargetObject(customService());
		writer.setTargetMethod("customWrite");
		return writer;
	}
	
	@Bean
	public CustomWriterService<String> customService() {
		return new CustomWriterService<String>();
	}
}
