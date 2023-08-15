package com.kbank.eai.job.tutorial;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class JdbcItemWriterJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Qualifier(value = "srcDataSource")
	private final DataSource srcDataSource;
	
	@Qualifier(value = "dstDataSource")
	private final DataSource dstDataSource;
	
	private final int chunkSize = 20;
	
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
				.<Map<String, Object>, Map<String, Object>>chunk(chunkSize)
				.reader(customItemReader())
//				.processor(null)
				.writer(jdbcBatchItemWriter())
				.build();
	}
	
	@Bean
	public JdbcPagingItemReader<Map<String, Object>> customItemReader() throws Exception {
		
		JdbcPagingItemReader<Map<String, Object>> reader = new JdbcPagingItemReader<>();
		reader.setDataSource(srcDataSource);
		reader.setPageSize(chunkSize);
		reader.setRowMapper((resultSet, rowNum) -> {
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("NAME", resultSet.getString("NAME"));
			resultMap.put("EMAIL", resultSet.getString("EMAIL"));
			resultMap.put("ADDRESS", resultSet.getString("ADDRESS"));
			resultMap.put("PHONE", resultSet.getString("PHONE"));
			return resultMap;
		});
		reader.setQueryProvider(createQueryProvider());
		return reader;
	}
	
	private PagingQueryProvider createQueryProvider() throws Exception {
		SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
		queryProvider.setDataSource(srcDataSource);
		queryProvider.setSelectClause("SELECT NAME, EMAIL, ADDRESS, PHONE");
		queryProvider.setFromClause("FROM USR_TBL");
		queryProvider.setSortKeys(Collections.singletonMap("NAME", Order.DESCENDING));
		return queryProvider.getObject();
	}
	
//	private ItemProcessor<Map<String, Object>, HashMap> processor() {
//		return map -> {
//			Map<String, Object> hashMap = new HashMap<>();
//			for(Map.Entry<String, Object> entry : map.entrySet()) {
//				
//			}
//		};
//	}
	
	@Bean
	public JdbcBatchItemWriter<Map<String, Object>> jdbcBatchItemWriter() {
	return new JdbcBatchItemWriterBuilder<Map<String, Object>>()
				.dataSource(dstDataSource)
				.sql("INSERT INTO USR_TBL (NAME, EMAIL, ADDRESS, PHONE) VALUES (:NAME, :EMAIL, :ADRESS, :PHONE)")
				.beanMapped()
				.build();
	}
	
}
