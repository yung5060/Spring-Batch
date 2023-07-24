package com.kbank.eai.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    private Long id;
    private String firstName;
    private String lastName;
    private String birthDate;

}
