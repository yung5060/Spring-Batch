package com.kbank.eai.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.kbank.eai.dto.DataSourceInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@PropertySource(value = "classpath:dbms/dbms_T.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "jdbc")
public class YamlConfig {
	
	private List<DataSourceInfo> list;
	
}
