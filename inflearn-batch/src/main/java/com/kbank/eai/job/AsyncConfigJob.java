package com.kbank.eai.job;

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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
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
				.start(step1())
				.build();
	}

	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
				.<HashMap, HashMap>chunk(chunkSize)
				.reader(pagingItemReader())
				.processor(customItemProcessor())
				.writer(customItemWriter())
				.build();
	}

	@Bean
	public Step asyncStep1() throws Exception {
		return stepBuilderFactory.get("asyncStep1")
				.chunk(chunkSize)
				.reader(pagingItemReader())
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
	public ItemWriter<? super HashMap> customItemWriter() throws Exception {
		return new MyBatisBatchItemWriterBuilder<HashMap>()
				.sqlSessionFactory(sqlSessionFactory_DST())
				.statementId(mapperName + ".insert")
				.build();
	}

	@Bean
	public ItemProcessor<? super HashMap, ? extends HashMap> customItemProcessor() {
		return new ItemProcessor<HashMap, HashMap>() {
			@Override
			public HashMap process(HashMap item) throws Exception {
				Map<String, String> result = new HashMap<>();
				item.forEach((key, value) -> {
					if("email".equals((String) key)) {
						result.put("email", item.get("email").toString().replaceFirst("eai", "fep"));
					}
					result.put((String) key, value + "_01");
				});
				System.out.println(result.toString());
				return (HashMap) result;
			}
		};
	}
}
