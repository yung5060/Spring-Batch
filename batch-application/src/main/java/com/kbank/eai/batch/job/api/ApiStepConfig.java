package com.kbank.eai.batch.job.api;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.kbank.eai.batch.chunk.processor.ApiItemProcessor1;
import com.kbank.eai.batch.chunk.processor.ApiItemProcessor2;
import com.kbank.eai.batch.chunk.processor.ApiItemProcessor3;
import com.kbank.eai.batch.chunk.writer.ApiItemWriter1;
import com.kbank.eai.batch.chunk.writer.ApiItemWriter2;
import com.kbank.eai.batch.chunk.writer.ApiItemWriter3;
import com.kbank.eai.batch.classifier.ProcessorClassifier;
import com.kbank.eai.batch.classifier.WriterClassifier;
import com.kbank.eai.batch.partition.ProductPartitioner;
import com.kbank.eai.domain.ApiRequestVO;
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
	public Step apiMasterStep() throws Exception {
		return stepBuilderFactory.get("apiMasterStep")
				.partitioner(apiSlaveStep().getName(), partitioner())
				.step(apiSlaveStep())
				.gridSize(3)
				.taskExecutor(taskExecutor())
				.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setMaxPoolSize(6);
		taskExecutor.setThreadNamePrefix("api-thread-");
		return taskExecutor;
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
	@StepScope
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
	
	
	@Bean
	public ItemProcessor itemProcessor() {
		ClassifierCompositeItemProcessor<ProductVO, ApiRequestVO> processor = new ClassifierCompositeItemProcessor<ProductVO, ApiRequestVO>();
		ProcessorClassifier<ProductVO, ItemProcessor<?, ? extends ApiRequestVO>> classifier = new ProcessorClassifier<>(); 
		Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap = new HashMap<>();
		processorMap.put("1", new ApiItemProcessor1());
		processorMap.put("2", new ApiItemProcessor2());
		processorMap.put("3", new ApiItemProcessor3());
		
		classifier.setProcessorMap(processorMap);
		
		processor.setClassifier(classifier);
		
		return processor;
	}
	
	@Bean
	public ItemWriter itemWriter() {
		ClassifierCompositeItemWriter<ApiRequestVO> writer = new ClassifierCompositeItemWriter<>();
		WriterClassifier<ApiRequestVO, ItemWriter<? super ApiRequestVO>> classifier = new WriterClassifier<>();
		Map<String, ItemWriter<ApiRequestVO>> writerMap = new HashMap<>();
		writerMap.put("1", new ApiItemWriter1());
		writerMap.put("2", new ApiItemWriter2());
		writerMap.put("3", new ApiItemWriter3());
		
		classifier.setWriterMap(writerMap);
		
		writer.setClassifier(classifier);
		
		return writer;
	}
}
