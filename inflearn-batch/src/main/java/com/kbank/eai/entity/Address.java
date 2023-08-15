package com.kbank.eai.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity(name = "address")
@ToString
public class Address {

	@Id
	@GeneratedValue
	private Long id;
	private String location;
	
}
