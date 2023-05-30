package com.kbank.eai.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.lang.Nullable;

public class CustomJobParametersIncrementer implements JobParametersIncrementer {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-hhmmss");

    @Override
    public JobParameters getNext(@Nullable JobParameters parameters) {

        String id = format.format(new Date());

        return new JobParametersBuilder().addString("run.id", id).toJobParameters();
    }
    
    
}
