package com.kbank.eai.job.template;

public class RetryableException extends RuntimeException {

    public RetryableException() {
        super();
    }

    public RetryableException(String message) {
        super(message);
        System.out.println(message);
    }
}
