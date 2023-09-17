package com.kbank.eai.job.tutorial;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.kbank.eai.listener.CustomChunkListener;
import com.kbank.eai.listener.StopWatchJobListener;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class AsyncConfigJob {

	@Value("${mapper}")
	private String mapperName;

	@Value("${chunk}")
	private int chunkSize;

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final ApplicationContext context;
	@Qualifier("srcDataSource")
	private final DataSource srcDataSource;
	@Qualifier("dstDataSource")
	private final DataSource dstDataSource;

	@Bean
	public SqlSessionFactory sqlSessionFactory_SRC() throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(srcDataSource);
		sessionFactoryBean.setMapperLocations(context.getResources("classpath:mappers/" + mapperName + ".xml"));
		return sessionFactoryBean.getObject();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory_DST() throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dstDataSource);
		sessionFactoryBean.setMapperLocations(context.getResources("classpath:mappers/" + mapperName + ".xml"));
		return sessionFactoryBean.getObject();
	}

	@Bean
	public Job batchJob() throws Exception {
		return jobBuilderFactory.get("batchJob")
				.incrementer(new RunIdIncrementer())
//				.start(step1())
				.start(asyncStep1())
				.listener(new StopWatchJobListener())
				.build();
	}

	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
				.<HashMap, HashMap>chunk(chunkSize)
				.reader(pagingItemReader())
				.processor(customItemProcessor())
				.writer(customItemWriter())
				.listener(new CustomChunkListener())
				.build();
	}

	@Bean
	public Step asyncStep1() throws Exception {
		return stepBuilderFactory.get("asyncStep1")
				.<HashMap, HashMap>chunk(chunkSize)
				.reader(pagingItemReader())
				.processor(asyncItemProcessor())
				.writer(asyncItemWriter())
				.listener(new CustomChunkListener())
				.build();
	}

	@Bean
	public MyBatisPagingItemReader<HashMap> pagingItemReader() throws Exception {
		return new MyBatisPagingItemReaderBuilder()
				.sqlSessionFactory(sqlSessionFactory_SRC())
				.queryId(mapperName + ".select")
				.pageSize(chunkSize)
				.build();
	}

	@Bean
	public ItemProcessor<HashMap, HashMap> customItemProcessor() throws InterruptedException {
		return new ItemProcessor<HashMap, HashMap>() {
			@Override
			public HashMap process(HashMap item) throws Exception {
				Thread.sleep(100);
				Map<String, String> result = new HashMap<>();
				item.forEach((key, value) -> {
					if("email".equals((String) key)) {
						result.put("email", item.get("email").toString().replaceFirst("fep", "mci"));
//					} else if ("phone".equals((String) key)){
//						result.put("phone", "+82" + " " + value.toString());
					} else {
						result.put((String) key, (String) value);
					}
				});
				System.out.println(result.toString());
				return (HashMap) result;
			}
		};
	}

	@Bean
	public ItemWriter<HashMap> customItemWriter() throws Exception {
		return new MyBatisBatchItemWriterBuilder<HashMap>()
				.sqlSessionFactory(sqlSessionFactory_DST())
				.statementId(mapperName + ".insert")
				.build();
	}

	@Bean
	public AsyncItemProcessor asyncItemProcessor() throws InterruptedException {
		AsyncItemProcessor<HashMap, HashMap> asyncItemProcessor = new AsyncItemProcessor<>();
		asyncItemProcessor.setDelegate(customItemProcessor());
		asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
//		asyncItemProcessor.afterPropertiesSet();
		return asyncItemProcessor;
	}

	@Bean
	public AsyncItemWriter asyncItemWriter() throws Exception {
		AsyncItemWriter<HashMap> asyncItemWriter = new AsyncItemWriter<>();
		asyncItemWriter.setDelegate(customItemWriter());
		return asyncItemWriter;
	}
}
