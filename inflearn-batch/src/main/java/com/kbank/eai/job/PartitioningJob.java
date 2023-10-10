package com.kbank.eai.job;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.kbank.eai.partitioner.ColumnRangePartitioner;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class PartitioningJob {

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
                .start(masterStep())
                .build();
    }

    @Bean
    public Step masterStep() throws Exception {
        return stepBuilderFactory.get("masterStep")
                .partitioner(slaveStep().getName(), partitioner())
                .step(slaveStep())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Step slaveStep() throws Exception {
        return stepBuilderFactory.get("slaveStep")
                .<HashMap, HashMap>chunk(chunkSize)
                .reader(pagingItemReader(null, null))
                .processor((ItemProcessor<HashMap, HashMap>) item -> {
                    HashMap<String, String> result = new HashMap<>();
                    item.forEach((key, value) -> {
                        if("email".equals((String) key)) {
                            result.put("email", ((String) value).replaceFirst("fep", "eai"));
                        } else {
                            result.put((String) key, (String) value);
                        }
                    });
//					System.out.println(result.toString());
                    return result;
                })
                .writer(pagingItemWriter())
                .build();
    }

    @Bean
    public Partitioner partitioner() {
        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();
        columnRangePartitioner.setColumn("id");
        columnRangePartitioner.setTable("customer2");
        columnRangePartitioner.setDataSource(srcDataSource);
        return columnRangePartitioner;
    }

    @Bean
    @StepScope
    public ItemReader<HashMap> pagingItemReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ) throws Exception {

        System.out.println("reading : " + minValue + " to " + maxValue);

        Map<String, Object> params = new HashMap<>();
        params.put("minValue", minValue);
        params.put("maxValue", maxValue);

        return new MyBatisPagingItemReaderBuilder<HashMap>()
                .sqlSessionFactory(sqlSessionFactory_SRC())
                .queryId(mapperName + ".select")
                .pageSize(chunkSize)
                .parameterValues(params)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<HashMap> pagingItemWriter() throws Exception {
        return new MyBatisBatchItemWriterBuilder<HashMap>()
                .sqlSessionFactory(sqlSessionFactory_DST())
                .statementId(mapperName + ".insert")
                .build();
    }
}
