package com.kbank.eai.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JpaConfig {
	
	@Qualifier(value = "srcDataSource")
	private final DataSource srcDataSource;
	
	
	@Bean(name = "CustomerEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
		emFactory.setDataSource(srcDataSource);
		emFactory.setPackagesToScan("com.kbank.eai.entity");
		emFactory.setPersistenceUnitName("customer");
		emFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		return emFactory;
	}
	
}
