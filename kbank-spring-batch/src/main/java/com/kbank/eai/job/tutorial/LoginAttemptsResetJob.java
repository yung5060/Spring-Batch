package com.kbank.eai.job.tutorial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class LoginAttemptsResetJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Qualifier(value = "srcDataSource")
	private final DataSource srcDataSource;
	
	@Bean
	public Job batchJob() {
		return jobBuilderFactory.get("loginResetJob")
				.incrementer(new RunIdIncrementer())
				.start(step1())
				.build();
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.tasklet((contribution, chunkContext) -> {
					 try (Connection connection = srcDataSource.getConnection()) {
				            // Create the update query
				            String updateQuery = "UPDATE IMG_USER SET LOGIN_ATTEMPTS = ? WHERE USER_ID = ?";
				            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

				            // Set the parameters for the query
				            preparedStatement.setString(1, "0");
				            preparedStatement.setString(2, "admin");

				            // Execute the update query
				            int rowsUpdated = preparedStatement.executeUpdate();

				            // Check the result
				            if (rowsUpdated > 0) {
				                System.out.println("Update successful! Rows affected: " + rowsUpdated);
				            } else {
				                System.out.println("Update failed or no matching records found.");
				            }
				        } catch (SQLException e) {
				            e.printStackTrace();
				        }
					return RepeatStatus.FINISHED;
				})
				.build();
	}
}
