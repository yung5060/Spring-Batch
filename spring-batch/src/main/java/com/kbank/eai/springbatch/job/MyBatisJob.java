package com.kbank.eai.springbatch.job;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbank.eai.springbatch.listener.MyBatisChunkListener;
import com.kbank.eai.springbatch.model.User;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MyBatisJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationContext context;
    private final MyBatisChunkListener listener;
    @Qualifier("srcDataSource")
    private final DataSource srcDataSource;
    @Qualifier("dstDataSource")
    private final DataSource dstDataSource;

    private final static int chunkSize = 3;

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
    public Job job() throws Exception {
        return jobBuilderFactory.get("myBatisJob")
                .start(step())
                .build();
    }

    @Bean
    public Step step() throws Exception {
        return stepBuilderFactory.get("myBatisStep")
                .<User,User>chunk(chunkSize)
                .reader(reader())
                .writer(writer())
                .listener(listener)
                .build();
    }

    @Bean
    public MyBatisPagingItemReader<User> reader() throws Exception {
        MyBatisPagingItemReader<User> reader = new MyBatisPagingItemReader<>();
        reader.setPageSize(chunkSize);
        reader.setSqlSessionFactory(sqlSessionFactory_SRC());
        reader.setQueryId("Source.findAll");
        return reader;
    }

    @Bean
    public MyBatisBatchItemWriter<User> writer() throws Exception {
        MyBatisBatchItemWriter<User> writer = new MyBatisBatchItemWriter<>();
        writer.setSqlSessionFactory(sqlSessionFactory_DST());
        writer.setStatementId("Destination.insert");
        return writer;
    }
}
