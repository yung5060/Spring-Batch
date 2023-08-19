package com.kbank.eai.job;

import com.kbank.eai.listener.CustomChunkListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ClassifierCompositeItemProcessorJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job batchJob() {
		return jobBuilderFactory.get("batchJob")
				.incrementer(new RunIdIncrementer())
				.start(step1())
				.build();
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<ProcessorInfo, ProcessorInfo>chunk(8)
				.reader(new ItemReader<ProcessorInfo>() {
					int i = 0;
					@Override
					@Nullable
					public ProcessorInfo read()
							throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
						i++;
						ProcessorInfo processorInfo = ProcessorInfo.builder().id(i % 3 + 1).build();
						return i > 30 ? null : processorInfo;
					}
					
				})
				.processor(customItemProcessor())
				.writer(items -> System.out.println(items.toString()))
				.listener(new CustomChunkListener())
				.build();
	}
	
	@Bean
	public ItemProcessor<? super ProcessorInfo, ? extends ProcessorInfo> customItemProcessor() {
		ClassifierCompositeItemProcessor<ProcessorInfo, ProcessorInfo> processor = new ClassifierCompositeItemProcessor<>();
		
		ProcessorClassifier<ProcessorInfo, ItemProcessor<?, ? extends ProcessorInfo>> classifier = new ProcessorClassifier<>();
		Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap = new HashMap<>();
		processorMap.put(1, new CustomItemProcessor());
		processorMap.put(2, new CustomItemProcessor2());
		processorMap.put(3, new CustomItemProcessor3());
		classifier.setProcessorMap(processorMap);
		processor.setClassifier(classifier);
		
		return processor;
	}
}
