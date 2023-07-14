package com.kbank.eai.util;


import com.kbank.eai.entity.Customer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(@NonNull Customer item) throws Exception {

        item.setName(item.getName().toUpperCase());

        return item;
    }
}
