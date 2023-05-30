package com.kbank.eai.springbatch.util;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.lang.Nullable;

public class CustomJobParametersValidator implements JobParametersValidator {

    @Override
    public void validate(@Nullable JobParameters parameters) throws JobParametersInvalidException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
    }
    
}
