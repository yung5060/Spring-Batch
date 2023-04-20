package com.kbank.eai.springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    
    private String name;
    private String email;
    private String address;
    private String phone;
}
