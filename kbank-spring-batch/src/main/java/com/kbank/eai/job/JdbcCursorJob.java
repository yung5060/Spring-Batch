package com.kbank.eai.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.kbank.eai.entity.Customer;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class JdbcCursorJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private int chunkSize = 10;
	
	@Qualifier(value = "srcDataSource")
	private final DataSource srcDataSource;
	
	@Bean
	public Job batchJob() {
		return jobBuilderFactory.get("batchJob")
				.start(step1())
				.build();
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<Customer, Customer>chunk(chunkSize)
				.reader(customItemReader())
				.writer(customItemWriter())
				.build();
	}
	
	@Bean
	public ItemReader<Customer> customItemReader() {
		return new JdbcCursorItemReaderBuilder<Customer>()
				.name("jdbcCursorItemReader")
				.fetchSize(chunkSize)
				.sql("select id, firstName, lastName, birthDate from customer where firstName like ? order by lastName, firstName")
				.beanRowMapper(Customer.class)
				.queryArguments("A%")
				.dataSource(srcDataSource)
				.build();
	}
	
	@Bean
	public ItemWriter<Customer> customItemWriter() {
		return items -> {
			for (Customer item : items) {
				System.out.println(item.toString());
			}
		};
	}
}
