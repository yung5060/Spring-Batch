package com.kbank.eai.job;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleBatchJobConfig {

	private final String mapperName;
	private final int chunkSize;
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final ApplicationContext context;
	private final DataSource srcDataSource;
	private final DataSource dstDataSource;
	
	@Autowired
	public SimpleBatchJobConfig(@Value("${mapper}") String mapperName, @Value("${chunk}") int chunkSize,
			JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ApplicationContext context,
			@Qualifier("srcDataSource") DataSource srcDataSource,
			@Qualifier("dstDataSource") DataSource dstDataSource) {
		super();
		this.mapperName = mapperName;
		this.chunkSize = chunkSize;
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.context = context;
		this.srcDataSource = srcDataSource;
		this.dstDataSource = dstDataSource;
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory_SRC() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean_SRC = new SqlSessionFactoryBean();
		sqlSessionFactoryBean_SRC.setDataSource(srcDataSource);
		sqlSessionFactoryBean_SRC.setMapperLocations(context.getResources("classpath:mappers/" + mapperName + ".xml"));
		return sqlSessionFactoryBean_SRC.getObject();
	}
	
	// 정확한 이유는 모르겠지만... 계속해서 SqlSessionTemplate 빈을 자동으로 생성하려고 하는데... 이때
	// 의존성(SqlSessionFactory) 주입 충돌이 일어나서 직접 주입해줌
	@Bean
	public SqlSessionTemplate sqlSessionTemplate_SRC() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory_SRC());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory_DST() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean_DST = new SqlSessionFactoryBean();
		sqlSessionFactoryBean_DST.setDataSource(dstDataSource);
		sqlSessionFactoryBean_DST.setMapperLocations(context.getResources("classpath:mappers/" + mapperName + ".xml"));
		return sqlSessionFactoryBean_DST.getObject();
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
				.reader(customItemReader())
				.processor((ItemProcessor<HashMap, HashMap>) item -> {
					HashMap<String, String> result = new HashMap<>();
					item.forEach((key, value) -> {
						if ("email".equals((String) key)) {
							result.put("email", ((String) value).replaceFirst("eai", "fep"));
						} else {
							result.put((String) key, (String) value);
						}
					});
					System.out.println(result.toString());
					return result;
				})
				.writer(customItemWriter())
				.build();
	}
	
	@Bean
	public ItemReader<HashMap> customItemReader() throws Exception {
		MyBatisPagingItemReader<HashMap> customItemReader = new MyBatisPagingItemReader<>();
		customItemReader.setSqlSessionFactory(sqlSessionFactory_SRC());
		customItemReader.setQueryId(mapperName + ".select");
		customItemReader.setPageSize(chunkSize);
		return customItemReader;
	}
	
	@Bean
	public ItemWriter<HashMap> customItemWriter() throws Exception {
		MyBatisBatchItemWriter<HashMap> customItemWriter = new MyBatisBatchItemWriter<HashMap>();
		customItemWriter.setSqlSessionFactory(sqlSessionFactory_DST());
		customItemWriter.setStatementId(mapperName + ".insert");
		return customItemWriter;
	}
	
	
}
