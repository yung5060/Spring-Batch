package com.kbank.eai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class Customer {

    private String name;
    private int age;
    private String year;

    public Customer(String name) {
        this.name = name;
        this.age = 20;
        this.year = "2000";
    }
}
