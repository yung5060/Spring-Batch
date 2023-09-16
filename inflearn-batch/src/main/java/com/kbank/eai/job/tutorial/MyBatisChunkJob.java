package com.kbank.eai.job.tutorial;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class MyBatisChunkJob {

    @Value("${mapper}")
    private String mapperName;

    @Value("${chunk}")
    private int chunkSize;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationContext context;
    private final ChunkListener chunkListener;
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
                .reader(myBatisPagingItemReader())
                .processor(processor())
                .writer(myBatisBatchItemWriter())
                .listener(chunkListener)
                .build();
    }

    @Bean
    public MyBatisPagingItemReader<HashMap> myBatisPagingItemReader() throws Exception {
        return new MyBatisPagingItemReaderBuilder<HashMap>()
                .sqlSessionFactory(sqlSessionFactory_SRC())
                .queryId(mapperName + ".select")
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public MyBatisBatchItemWriter<HashMap> myBatisBatchItemWriter() throws Exception {
        return new MyBatisBatchItemWriterBuilder<HashMap>()
                .sqlSessionFactory(sqlSessionFactory_DST())
                .statementId(mapperName + ".insert")
                .build();
    }

    private ItemProcessor<? super HashMap, ? extends HashMap> processor() {
        return new ItemProcessor<HashMap, HashMap>() {
            @Override
            public HashMap process(HashMap item) throws Exception {
                System.out.println(item.toString());
                Map<String, String> result = new HashMap<>();
                item.forEach((key, value) -> {
                    String val = value.toString().replaceFirst("eai", "fep");
                    result.put((String) key, val);
                });
                System.out.println(result.toString());
                return (HashMap) result;
            }
        };
    }
}
