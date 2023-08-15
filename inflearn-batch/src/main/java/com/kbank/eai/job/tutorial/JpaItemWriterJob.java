package com.kbank.eai.job.tutorial;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.kbank.eai.entity.Customer2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Configuration
@RequiredArgsConstructor
public class JpaItemWriterJob {
	
	private final int chunkSize = 7;

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final ChunkListener customChunkListener;
	
	@Qualifier(value = "EntityManagerFactory_SRC")
	private final EntityManagerFactory entityManagerFactory_SRC;
	@Qualifier(value = "EntityManagerFactory_DST")
	private final EntityManagerFactory entityManagerFactory_DST;
	
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
				.<Customer2, Customer2>chunk(chunkSize)
				.reader(customItemReader())
				.processor(customItemProcessor())
				.writer(customItemWriter())
				.listener(customChunkListener)
				.build();
	}
	
	@Bean
	public ItemReader<Customer2> customItemReader() {
		return new JpaPagingItemReaderBuilder<Customer2>()
				.name("jpaPagingItemReader")
				.entityManagerFactory(entityManagerFactory_SRC)
				.pageSize(chunkSize)
				.queryString("select c from customer2 c")
				.build();
	}
	
	@Bean
	public ItemProcessor<? super Customer2, ? extends Customer2> customItemProcessor() {
		return new ItemProcessor<Customer2, Customer2>() {
			@Override
			@Nullable
			public Customer2 process(@NonNull Customer2 item) throws Exception {
				Customer2 customer = new Customer2();
				customer.setName(item.getName());
				customer.setEmail(item.getEmail().replaceFirst("mci", "eai"));
				customer.setAddress(item.getAddress());
				customer.setPhone(item.getPhone());
				log.info(customer.toString());
				return customer;
			}
			
		};
	}
	
	@Bean
	public ItemWriter<? super Customer2> customItemWriter() {
		return new JpaItemWriterBuilder<Customer2>()
				.entityManagerFactory(entityManagerFactory_DST)
				.build();
	}
}
