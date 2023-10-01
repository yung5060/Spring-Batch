package com.kbank.eai.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;

public class CustomTasklet implements Tasklet {

//	@Override
//	@Nullable
//	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//		
//		String stepName = contribution.getStepExecution().getStepName();
//		String jobName = chunkContext.getStepContext().getJobName();
//		
//		System.out.println("jobName = " + jobName);
//		System.out.println("stepName = " + stepName);
//		
//		return RepeatStatus.FINISHED;
//	}
	
//	private AtomicLong sum = new AtomicLong(0L);
	private long sum;
	private Object lock = new Object();

	@Override
	@Nullable
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		synchronized (lock) {
			for(int i = 0; i < 1000000000; i++) {
//				sum.incrementAndGet();
				sum++;
			}
			System.out.println(String.format("%s has been executed on thread %s", chunkContext.getStepContext().getStepName(), Thread.currentThread().getName()));
			System.out.println(String.format("sum : %d", sum));
		}
		return RepeatStatus.FINISHED;
	}
}
