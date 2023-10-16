package com.kbank.eai;

import java.util.Date;

import javax.batch.runtime.BatchStatus;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.kbank.eai.job.SimpleBatchJobConfig;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes={SimpleBatchJobConfig.class, TestBatchConfig.class})
public class SimpleBatchJobTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Test
	public void simpleJob_test() throws Exception {
		
		//given
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("name", "user1")
				.addLong("date", new Date().getTime())
				.toJobParameters();
		
		//when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
		
		//then
		Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
		Assert.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
		
	}
	
	@After
	public void clear() {
		jdbcTemplate.execute("truncate table customer2");
	}
	
}
