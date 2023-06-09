package com.kbank.eai.springbatch.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.jasypt.encryption.StringEncryptor;
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
	private final StringEncryptor encryptor;
	
//	@Primary
	@Bean
	public DataSource dataSource() throws SQLException {
		DataSource ds = null;
		return findDs("LOCAL", ds);
	}

//	@Bean(name = "logDataSource")
//	public DataSource logDataSource() throws SQLException {
//		DataSource ds = null;
//		return findDs("LOG", ds);
//	}

	@Bean(name = "srcDataSource")
	public DataSource srcDataSource(@Value("${sBiz}") String sBiz) throws SQLException {
		DataSource ds = null;
		return findDs(sBiz, ds);
	}

	@Bean(name = "dstDataSource")
	public DataSource dstDataSource(@Value("${dBiz}") String dBiz) throws SQLException {
		DataSource ds = null;
		ds = findDs(dBiz, ds);
		String query = "CREATE TABLE IF NOT EXISTS USER_TBL"
						+ "(NAME VARCHAR(200),"
						+ "    EMAIL VARCHAR(200),"
						+ "    ADDRESS VARCHAR(200),"
						+ "    PHONE VARCHAR(200))";
		Connection connection = ds.getConnection();
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
		statement.close();
		connection.close();
		return ds;
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
