package com.kbank.eai.job;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbank.eai.entity.Customer2;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JpaItemWriterJob {
	
	private final int chunkSize = 5;

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Qualifier(value = "srcDataSource")
	private final DataSource srcDataSource;
	@Qualifier(value = "dstDataSource")
	private final DataSource dstDataSource;
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
				.writer(writer())
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
	
	private ItemWriter<Customer2> writer() {
		return customers -> {
			System.out.println("\n==========================chunk done================================\n");
			for (Customer2 customer : customers) {
				System.out.println(customer.toString());
			}
		};
	}
}
