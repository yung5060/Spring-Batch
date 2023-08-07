package com.kbank.eai.job.tutorial;

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

import com.kbank.eai.entity.Customer;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class JpaPagingItemReaderJob {
	
	private final int chunkSize = 7;

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Qualifier(value = "srcDataSource")
	private final DataSource srcDataSource;
	@Qualifier(value = "CustomerEntityManagerFactory")
	private final EntityManagerFactory entityManagerFactory;
	
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
				.<Customer, Customer>chunk(chunkSize)
				.reader(customItemReader())
				.writer(writer())
				.build();
	}
	
	@Bean
	public ItemReader<Customer> customItemReader() {
		return new JpaPagingItemReaderBuilder<Customer>()
				.name("jpaPagingItemReader")
				.entityManagerFactory(entityManagerFactory)
				.pageSize(chunkSize)
				.queryString("select c from customer c")
				.build();
	}
	
	private ItemWriter<Customer> writer() {
		return customers -> {
			System.out.println("chunk done! ...");
			for (Customer customer : customers) 
				System.out.println(customer.toString());
		};
	}
}
