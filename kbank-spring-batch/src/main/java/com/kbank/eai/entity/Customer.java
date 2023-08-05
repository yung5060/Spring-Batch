package com.kbank.eai.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity(name = "customer")
@ToString
public class Customer {

	@Id
	@GeneratedValue
    private Long id;
    private String username;
    private int age;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
}
