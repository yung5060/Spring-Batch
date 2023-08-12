package com.kbank.eai.springbatch.job;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbank.eai.springbatch.listener.MyBatisChunkListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EaiJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationContext context;
    private final MyBatisChunkListener listener;
    
    @Qualifier("srcDataSource")
    private final DataSource srcDataSource;
    @Qualifier("dstDataSource")
    private final DataSource dstDataSource;

    private final static int chunkSize = 15;

    @Bean
    public SqlSessionFactory sqlSessionFactory_SRC() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(srcDataSource);
        sessionFactoryBean.setMapperLocations(context.getResources("classpath:mappers/*.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory_DST() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dstDataSource);
        sessionFactoryBean.setMapperLocations(context.getResources("classpath:mappers/*.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean
    public Job userJob() throws Exception {
        return jobBuilderFactory.get("EaiJob")
                .start(userStep())
                .build();
    }

    @Bean
    public Step userStep() throws Exception {
        return stepBuilderFactory.get("userStep")
                .<HashMap, HashMap>chunk(chunkSize)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .listener(listener)
                .build();
    }

    @Bean
    public MyBatisPagingItemReader<HashMap> reader() throws Exception {
        MyBatisPagingItemReader<HashMap> reader = new MyBatisPagingItemReader<>();
        reader.setPageSize(chunkSize);
        reader.setSqlSessionFactory(sqlSessionFactory_SRC());
        reader.setQueryId("Mapper.findAll");
        return reader;
    }

    @Bean
    public ItemProcessor<HashMap, HashMap> processor() {
        return map -> {
        	log.info(map.toString());
        	return map;
        };
    }

    @Bean
    public MyBatisBatchItemWriter<HashMap> writer() throws Exception {
        MyBatisBatchItemWriter<HashMap> writer = new MyBatisBatchItemWriter<>();
        writer.setSqlSessionFactory(sqlSessionFactory_DST());
        writer.setStatementId("Mapper.insert");
        // log.info();
        return writer;
    }
}
