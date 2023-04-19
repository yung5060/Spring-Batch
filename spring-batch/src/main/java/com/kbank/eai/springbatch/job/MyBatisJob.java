package com.kbank.eai.springbatch.job;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MyBatisJob {
    
    private final ApplicationContext context;
    @Qualifier("logDataSource")
    private final DataSource logDataSource;
    @Qualifier("srcDataSource")
    private final DataSource srcDataSource;
    @Qualifier("dstDataSource")
    private final DataSource dstDataSource;

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
    public SqlSessionFactory sqlSessionFactory_LOG() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(logDataSource);
        sessionFactoryBean.setMapperLocations(context.getResources("classpath:mappers/*.xml"));
        return sessionFactoryBean.getObject();
    }
}
