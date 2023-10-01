package com.kbank.eai.job;

import com.kbank.eai.listener.StopWatchJobListener;
import com.kbank.eai.tasklet.CustomTasklet;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ParallelStepConfigJob {

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
    public Job batchJob() {
        return jobBuilderFactory.get("batchJob")
                .incrementer(new RunIdIncrementer())
                .start(flow1())
//				.next(flow2())
                .split(taskExecutor()).add(flow2())
                .end()
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Flow flow1() {
        TaskletStep step1 = stepBuilderFactory.get("step1")
                .tasklet(tasklet())
                .build();
        return new FlowBuilder<Flow>("flow1")
                .start(step1)
                .build();
    }

    @Bean
    public Flow flow2() {
        TaskletStep step2 = stepBuilderFactory.get("step2")
                .tasklet(tasklet())
                .build();
        TaskletStep step3 = stepBuilderFactory.get("step3")
                .tasklet(tasklet())
                .build();
        return new FlowBuilder<Flow>("flow2")
                .start(step2)
                .next(step3)
                .build();
    }


    @Bean
    public Tasklet tasklet() {
        return new CustomTasklet();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("async_thread_");
        return executor;
    }
}
