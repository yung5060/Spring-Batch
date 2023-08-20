package com.kbank.eai.job;


import java.text.ParseException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RepeatJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job batchJob() throws Exception {
		return jobBuilderFactory.get("batchJob")
				.incrementer(new RunIdIncrementer())
				.start(step())
				.build();
	}
	
	@Bean
	public Step step() throws Exception {
		return stepBuilderFactory.get("step")
				.<String, String>chunk(5)
				.reader(new ItemReader<String>() {
					int i = 0;
					@Override
					public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
						i++;
						return i > 3 ? null : "item" + i;
					}
				})
				.processor(new ItemProcessor<String, String>() {
					
					RepeatTemplate repeatTemplate = new RepeatTemplate();
					
					@Override
					public String process(String item) throws Exception {
						
						repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));
//						repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(1000));
						
						repeatTemplate.iterate(new RepeatCallback() {
							@Override
							public RepeatStatus doInIteration(RepeatContext context) throws Exception {
								
								System.out.println("repeatTemplate is being tested");
								return RepeatStatus.CONTINUABLE;
							}
						});
						
						System.out.println("test done.");
						return item;
					}
				})
				.writer(items -> System.out.println(items))
				.build();
	}
}
