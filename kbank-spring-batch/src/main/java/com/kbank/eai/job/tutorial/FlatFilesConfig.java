package com.kbank.eai.job.tutorial;

import com.kbank.eai.entity.Customer;
import com.kbank.eai.util.mapper.CustomerFieldSetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

//@Configuration
@RequiredArgsConstructor
public class FlatFilesConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob() {
        return jobBuilderFactory.get("batchJob")
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(3)
                .reader(itemReader())
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> items) throws Exception {
                        System.out.println("items = " + items);
                    }
                })
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2 has been executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public ItemReader itemReader() {
//        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//        itemReader.setResource(new ClassPathResource("/flatfiles/customer.csv"));
//
//        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
//        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
//        lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
//
//        itemReader.setLineMapper(lineMapper);
//        itemReader.setLinesToSkip(1);

//        return itemReader;

        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFile")
                .resource(new ClassPathResource("/flatfiles/customer.csv"))
                .fieldSetMapper(new CustomerFieldSetMapper())
                .linesToSkip(1)
                .delimited().delimiter(",")
                .names("name", "age", "year")
                .build();
    }
}
