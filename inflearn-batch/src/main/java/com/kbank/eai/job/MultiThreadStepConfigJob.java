package com.kbank.eai.job;

import java.util.HashMap;

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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.kbank.eai.listener.CustomChunkListener;
import com.kbank.eai.listener.CustomItemProcessListener;
import com.kbank.eai.listener.CustomItemReadListener;
import com.kbank.eai.listener.CustomItemWriteListener;
import com.kbank.eai.listener.StopWatchJobListener;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MultiThreadStepConfigJob {
	
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
				.start(step1())
				.listener(new StopWatchJobListener())
				.build();
	}
	
	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
				.<HashMap, HashMap>chunk(chunkSize)
				.reader(pagingItemReader())
				.listener(new CustomItemReadListener())
				.processor((ItemProcessor<HashMap, HashMap>) item -> {
					HashMap<String, String> result = new HashMap<>();
					item.forEach((key, value) -> {
						if("email".equals((String) key)) {
							result.put("email", ((String) value).replaceFirst("fep", "mci"));
						} else {
							result.put((String) key, (String) value);
						}
					});
//					System.out.println(result.toString());
					return result;
				})
				.listener(new CustomItemProcessListener())
				.writer(customItemWriter())
				.listener(new CustomItemWriteListener())
//				.taskExecutor(taskExecutor())
				.listener(new CustomChunkListener())
				.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setMaxPoolSize(8);
		taskExecutor.setThreadNamePrefix("async_thread_");
		return taskExecutor;
	}
	
	@Bean
	public MyBatisPagingItemReader<HashMap> pagingItemReader() throws Exception {
		return new MyBatisPagingItemReaderBuilder<HashMap>()
				.sqlSessionFactory(sqlSessionFactory_SRC())
				.queryId(mapperName + ".select")
				.pageSize(chunkSize)
				.build();
	}
	
	@Bean
	public ItemWriter<HashMap> customItemWriter() throws Exception {
		return new MyBatisBatchItemWriterBuilder<HashMap>()
				.sqlSessionFactory(sqlSessionFactory_DST())
				.statementId(mapperName + ".insert")
				.build();
	}
}
