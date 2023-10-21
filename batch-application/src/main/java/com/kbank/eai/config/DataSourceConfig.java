package com.kbank.eai.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.kbank.eai.dto.DataSourceInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

	private final YamlConfig yamlConfig;
	private final StringEncryptor encryptor;
	
	@Bean
	@Primary
	public DataSource dataSource() throws SQLException {
		DataSource ds = null;
		return findDs("LOCAL", ds);
	}


	@Bean(name = "srcDataSource")
	public DataSource srcDataSource(@Value("${sBiz}") String sBiz) throws SQLException {
		log.info("SRC: " + sBiz);
		DataSource ds = null;
		return findDs(sBiz, ds);
	}
	

	@Bean(name = "dstDataSource")
	public DataSource dstDataSource(@Value("${dBiz}") String dBiz) throws SQLException {
		log.info("DST: " + dBiz);
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
						.password(encryptor.decrypt(info.getPassword()))
						.build();
				return ds;
			}
		}
		return ds;
	}
}
