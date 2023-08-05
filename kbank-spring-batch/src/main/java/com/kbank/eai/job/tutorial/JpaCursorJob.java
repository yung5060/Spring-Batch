package com.kbank.eai.job.tutorial;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.kbank.eai.entity.Customer;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class JpaCursorJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
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
				.<Customer, Customer>chunk(2)
				.reader(customItemReader())
				.writer(customItemWriter())
				.build();
	}
	
	@Bean
	public ItemReader<? extends Customer> customItemReader() {
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("firstname", "%a%");
		
		return new JpaCursorItemReaderBuilder<Customer>()
				.name("jpaCursorItemReader")
				.entityManagerFactory(entityManagerFactory)
				.queryString("select c from customer c where firstname like :firstname order by id asc")
				.parameterValues(parameters)
				.build();
	}
	
	@Bean
	public ItemWriter<Customer> customItemWriter() {
		return items -> {
			System.out.println(items.toString());
		};
	}
}
