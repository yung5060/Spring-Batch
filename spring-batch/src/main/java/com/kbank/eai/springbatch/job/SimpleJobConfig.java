package com.kbank.eai.springbatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SimpleJobConfig {
    
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1())
                .next(simpleStep2())
                .next(simpleStep3())
                .build();
    }

    @Bean
    public Step simpleStep1() {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((contribution, chunckContext) -> {
                    System.out.println("simpleStep1 has been executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step simpleStep2() {
        return stepBuilderFactory.get("simpleStep2")
                .tasklet((contribution, chunckContext) -> {
                    System.out.println("simpleStep2 has been executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step simpleStep3() {
        return stepBuilderFactory.get("simpleStep3")
                .tasklet((contribution, chunckContext) -> {
                    System.out.println("simpleStep3 has been executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
