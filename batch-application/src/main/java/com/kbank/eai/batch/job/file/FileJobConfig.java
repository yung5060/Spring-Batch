package com.kbank.eai.batch.job.file;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.kbank.eai.batch.chunk.processor.FileItemProcessor;
import com.kbank.eai.domain.Product;
import com.kbank.eai.domain.ProductVO;

@Configuration
public class FileJobConfig {

	private final int chunkSize;
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
//	private final EntityManagerFactory srcEntityManagerFactory;
	private final EntityManagerFactory dstEntityManagerFactory;

	@Autowired
	public FileJobConfig(@Value("${chunk}")int chunkSize, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory
//			, @Qualifier("EntityManagerFactory_SRC") EntityManagerFactory srcEntityManagerFactory
			, @Qualifier("EntityManagerFactory_DST") EntityManagerFactory dstEntityManagerFactory) {
		this.chunkSize = chunkSize;
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
//		this.srcEntityManagerFactory = srcEntityManagerFactory;
		this.dstEntityManagerFactory = dstEntityManagerFactory;
	}
	
	@Bean
	public Job fileJob() {
		return jobBuilderFactory.get("fileJob")
				.incrementer(new RunIdIncrementer())
				.start(fileStep())
				.build();
	}

	@Bean
//	@JobScope
	public Step fileStep() {
		return stepBuilderFactory.get("fileStep")
				.<ProductVO, Product>chunk(chunkSize)
				.reader(fileItemReader(null))
				.processor(fileItemProcessor())
				.writer(fileItemWriter())
				.listener(new ChunkListener() {
					
					@Override
					public void beforeChunk(ChunkContext context) {}
					
					@Override
					public void afterChunkError(ChunkContext context) {}
					
					@Override
					public void afterChunk(ChunkContext context) {
						System.out.println("==============chunk done!!!==============");
					}
				})
				.build();
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<ProductVO> fileItemReader(@Value("#{jobParameters['requestDate']}") String requestDate) {
		return new FlatFileItemReaderBuilder<ProductVO>()
				.name("flatFile")
				.resource(new ClassPathResource("flatfiles/product_" + requestDate + ".csv"))
				.fieldSetMapper(new BeanWrapperFieldSetMapper<>())
				.targetType(ProductVO.class)
				.linesToSkip(0)
				.delimited().delimiter(",")
				.names("id", "name", "price", "type")
				.build();
	}
	
	@Bean
	public ItemProcessor<ProductVO, Product> fileItemProcessor() {
		return new FileItemProcessor();
	}
	
	@Bean
	public ItemWriter<Product> fileItemWriter() {
		return new JpaItemWriterBuilder<Product>()
				.entityManagerFactory(dstEntityManagerFactory)
				.usePersist(true)
				.build();
	}
}
