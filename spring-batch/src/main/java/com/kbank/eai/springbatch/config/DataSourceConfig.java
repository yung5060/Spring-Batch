package com.kbank.eai.springbatch.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbank.eai.springbatch.dto.DataSourceInfo;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

	private final YamlConfig yamlConfig;
	
	@Bean
	public DataSource dataSource() throws SQLException {
		DataSource ds = null;
		return findDs("LOCAL", ds);
	}

	@Bean(name = "logDataSource")
	public DataSource logDataSource() throws SQLException {
		DataSource ds = null;
		return findDs("LOG", ds);
	}

	@Bean(name = "srcDataSource")
	public DataSource srcDataSource(@Value("${sBiz}") String sBiz) throws SQLException {
		DataSource ds = null;
		return findDs(sBiz, ds);
	}

	@Bean(name = "dstDataSource")
	public DataSource dstDataSource(@Value("${dBiz}") String dBiz) throws SQLException {
		DataSource ds = null;
		return findDs(dBiz, ds);
	}

	private DataSource findDs(String biz, DataSource ds) throws SQLException {
		for (DataSourceInfo info : yamlConfig.getList()) {
			if(info.getSystem().equals(biz)) {
				ds = DataSourceBuilder.create()
						.driverClassName(info.getDriverClassName())
						.url(info.getUrl())
						.username(info.getUsername())
						.password(info.getPassword())
						.build();
				return ds;
			}
		}
		return ds;
	}
}
