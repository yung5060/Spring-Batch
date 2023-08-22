package com.kbank.eai.job.tutorial;


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
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;

//@Configuration
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
				.<String, String>chunk(3)
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
						
//						repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));
//						repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(1000));
						
//						CompositeCompletionPolicy completionPolicy = new CompositeCompletionPolicy();
//						CompletionPolicy[] completionPolicies = new CompletionPolicy[] {
//																	new SimpleCompletionPolicy(3),
//																	new TimeoutTerminationPolicy(3000)
//																};
//						completionPolicy.setPolicies(completionPolicies);
//						repeatTemplate.setCompletionPolicy(completionPolicy);
						
						repeatTemplate.setExceptionHandler(simpleExceptionHandler());
						
						repeatTemplate.iterate(new RepeatCallback() {
							@Override
							public RepeatStatus doInIteration(RepeatContext context) throws Exception {
								
								System.out.println("repeatTemplate is being tested");
								throw new RuntimeException("Exception has occured");
//								return RepeatStatus.CONTINUABLE;
							}
						});
						
						System.out.println("test done.");
						return item;
					}
				})
				.writer(items -> System.out.println(items))
				.build();
	}
	
	@Bean
	public ExceptionHandler simpleExceptionHandler() {
		return new SimpleLimitExceptionHandler(3);
	}
}
