package com.kbank.eai.job;

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
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import lombok.RequiredArgsConstructor;

@Configuration
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
				.<Map, HashMap>chunk(chunkSize)
				.reader(customItemReader())
//				.processor(null)
				.writer(jdbcBatchItemWriter())
				.build();
	}
	
	@Bean
	public JdbcPagingItemReader<Map<String, Object>> customItemReader() throws Exception {
		
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("NAME", Order.ASCENDING);
		
		return new JdbcPagingItemReaderBuilder<Map<String, Object>>()
				.name("jdbcPagingItemReader")
				.dataSource(srcDataSource)
				.rowMapper(new ColumnMapRowMapper())
				.pageSize(chunkSize)
				.queryProvider(createQueryProvider())
				.sortKeys(sortKeys)
				.build();
	}
	
	private PagingQueryProvider createQueryProvider() throws Exception {
		SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
		queryProvider.setDataSource(srcDataSource);
		queryProvider.setSelectClause("SELECT *");
		queryProvider.setFromClause("FROM USR_TBL");
		queryProvider.setSortKey("NAME");
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
	public JdbcBatchItemWriter<HashMap> jdbcBatchItemWriter() {
		return new JdbcBatchItemWriterBuilder<HashMap>()
				.dataSource(dstDataSource)
				.sql("INSERT INTO USER_TBL (NAME, EMAIL, ADDRESS, PHONE) VALUES (:NAME, :EMAIL, :ADRESS, :PHONE)")
				.beanMapped()
				.build();
	}
}
