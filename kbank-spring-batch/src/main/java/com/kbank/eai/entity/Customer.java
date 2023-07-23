package com.kbank.eai.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    private long id;
    private String name;
    private int age;

}
