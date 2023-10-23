package com.kbank.eai.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Product {

	@Id
	private Long id;
	private String name;
	private int price;
	private String type;
}
