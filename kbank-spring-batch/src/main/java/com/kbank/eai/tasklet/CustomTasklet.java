package com.kbank.eai.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;

public class CustomTasklet implements Tasklet {

	@Override
	@Nullable
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		String stepName = contribution.getStepExecution().getStepName();
		String jobName = chunkContext.getStepContext().getJobName();
		
		System.out.println("jobName = " + jobName);
		System.out.println("stepName = " + stepName);
		
		return RepeatStatus.FINISHED;
	}

	
}
