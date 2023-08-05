package com.kbank.eai.config;

import java.util.Properties;

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
		emFactory.setJpaProperties(jpaProperties());
		return emFactory;
	}
	
	// Define any additional JPA properties if needed
    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "update"); // Set the ddl-auto mode here
        properties.put("hibernate.show_sql", "true"); // Enable SQL query output to the console
        properties.put("hibernate.format_sql", "true"); // Optional: Format the SQL queries nicely
        // Add any other required properties
        return properties;
    }
    
//    jpa:
//        hibernate:
//            ddl-auto: update
//        database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
//        show-sql: true
//        properties:
//            hibernate.format_sql: true
}
