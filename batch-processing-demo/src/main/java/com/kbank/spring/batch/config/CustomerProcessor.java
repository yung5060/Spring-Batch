package com.kbank.spring.batch.config;

import org.springframework.batch.item.ItemProcessor;

import com.kbank.spring.batch.entity.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer item) throws Exception {
		return null;
	}
	
	

}
