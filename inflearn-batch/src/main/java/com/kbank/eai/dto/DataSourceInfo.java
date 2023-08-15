package com.kbank.eai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceInfo {

	private String system;
	private String driverClassName;
	private String url;
	private String username;
	private String password;
}
