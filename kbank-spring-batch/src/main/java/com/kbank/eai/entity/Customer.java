package com.kbank.eai.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    private String name;
    private String age;
    private String year;

    public Customer(String name) {
        this.name = name;
        this.age = "20";
        this.year = "2000";
    }
}
