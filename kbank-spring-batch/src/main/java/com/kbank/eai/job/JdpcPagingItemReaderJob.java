package com.kbank.eai.job;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.kbank.eai.entity.Customer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JdpcPagingItemReaderJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Qualifier(value = "srcDataSource")
	private final DataSource srcDataSource;
	
	@Bean
	public Job batchJob() throws Exception {
		return jobBuilderFactory.get("batchJob")
				.incrementer(new RunIdIncrementer())
				.start(step1())
				.build();
	}
	
	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
				.<Customer, Customer>chunk(2)
				.reader(customItemReader())
				.writer(writer())
				.build();
	}
	
	@Bean
	public ItemReader<? extends Customer> customItemReader() throws Exception {
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("firstname", "%e%");
		
		return new JdbcPagingItemReaderBuilder<Customer>()
				.name("jdbcPagingReader")
				.pageSize(2)
				.dataSource(srcDataSource)
				.rowMapper(new BeanPropertyRowMapper<>(Customer.class))
				.queryProvider(createQueryProvider())
				.parameterValues(parameters)
				.build();
	}
	
	private PagingQueryProvider createQueryProvider() throws Exception {
		
		SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
		queryProvider.setDataSource(srcDataSource);
		queryProvider.setSelectClause("id, firstname, lastname, birthdate");
		queryProvider.setFromClause("from customer");
		queryProvider.setWhereClause("where firstname like :firstname");
		
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("id", Order.ASCENDING);
		
		queryProvider.setSortKeys(sortKeys);
		
		return queryProvider.getObject();
	}
	
	private ItemWriter<Customer> writer() {
		return customers -> {
			System.out.println("chunk done! ...");
			for (Customer customer : customers) {
				System.out.println(customer.toString());
			}
		};
	}
}
