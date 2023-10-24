package com.kbank.eai.batch.job.api;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.kbank.eai.batch.partition.ProductPartitioner;
import com.kbank.eai.domain.ProductVO;
import com.kbank.eai.util.QueryGenerator;

@Configuration
public class ApiStepConfig {

	private final int chunkSize;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dstDataSource;
	
	@Autowired
	public ApiStepConfig(StepBuilderFactory stepBuilderFactory, @Qualifier("dstDataSource") DataSource dstDataSource) {
		this.chunkSize = 10;
		this.stepBuilderFactory = stepBuilderFactory;
		this.dstDataSource = dstDataSource;
	}
	
	@Bean
	public Step apiMasterStep() {
		return stepBuilderFactory.get("apiMasterStep")
				.partitioner(apiSlaveStep().getName(), partitioner())
				.step(apiSlaveStep())
				.gridSize(3)
				.taskExecutor(taskExecutor())
				.build();
	}
	
	@Bean
	public Step apiSlaveStep() throws Exception {
		return stepBuilderFactory.get("apiSlaveStep")
				.<ProductVO, ProductVO>chunk(chunkSize)
				.reader(itemReader(null))
				.processor(itemProcessor())
				.writer(itemWriter())
				.build();
	}
	
	@Bean
	public ProductPartitioner partitioner() {
		ProductPartitioner productPartitioner = new ProductPartitioner();
		productPartitioner.setDataSource(dstDataSource);
		return productPartitioner;
	}
	
	@Bean
	public ItemReader<ProductVO> itemReader(@Value("#{stepExecutionContext['product']}") ProductVO productVO) throws Exception {
		
		JdbcPagingItemReader<ProductVO> reader = new JdbcPagingItemReader<>();
		
		reader.setDataSource(dstDataSource);
		reader.setPageSize(chunkSize);
		reader.setRowMapper(new BeanPropertyRowMapper<>(ProductVO.class));
		
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, name, price, type");
		queryProvider.setFromClause("from product");
		queryProvider.setWhereClause("where type = :type");
		
		Map<String, Order> sortKeys = new HashMap<>(1);
		sortKeys.put("id", Order.DESCENDING);
		queryProvider.setSortKeys(sortKeys);
		
		reader.setParameterValues(QueryGenerator.getParameterForQuery("type", productVO.getType()));
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();
		
		return reader;
	}
}
