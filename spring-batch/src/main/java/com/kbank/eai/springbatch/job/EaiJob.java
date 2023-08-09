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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import com.kbank.eai.springbatch.listener.MyBatisChunkListener;
import com.kbank.eai.springbatch.model.User;

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

    private final static int chunkSize = 2;

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
                .<User, User>chunk(chunkSize)
                .reader(reader())
                .processor(processor())
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
    public ItemProcessor<User, User> processor() {
        return new ItemProcessor<User, User>() {
            @Override
            public User process(@NonNull User user) throws Exception {
                user.setEmail(user.getEmail().replaceFirst("now", "withu"));
                log.info(user.getName() + " processed!");
                return user;
            }
        };
    }

    @Bean
    public MyBatisBatchItemWriter<User> writer() throws Exception {
        MyBatisBatchItemWriter<User> writer = new MyBatisBatchItemWriter<>();
        writer.setSqlSessionFactory(sqlSessionFactory_DST());
        writer.setStatementId("Destination.insert");
        // log.info();
        return writer;
    }
}
