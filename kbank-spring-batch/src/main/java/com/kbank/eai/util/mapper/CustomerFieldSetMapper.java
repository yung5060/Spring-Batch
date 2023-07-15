package com.kbank.eai.util.mapper;

import com.kbank.eai.entity.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {


    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {

        if(fieldSet == null) {
            return null;
        }

        return Customer.builder()
                .name(fieldSet.readString("name"))
                .age(fieldSet.readInt("age"))
                .year(fieldSet.readString("year"))
                .build();
    }
}
