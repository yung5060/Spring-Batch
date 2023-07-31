package com.kbank.eai.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


import lombok.Data;

@Data
@Entity
public class Customer {

	@Id
	@GeneratedValue
    private long id;
    private String firstname;
    private String lastname;
    private String birthdate;

}
