package com.kbank.eai.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class JpaConfig {
	
	private final DataSource srcDataSource;
	private final DataSource dstDataSource;
	
	@Autowired
	public JpaConfig(@Qualifier("srcDataSource") DataSource srcDataSource, @Qualifier("dstDataSource") DataSource dstDataSource) {
		this.srcDataSource = srcDataSource;
		this.dstDataSource = dstDataSource;
	}
	
	@Bean
	@Qualifier("EntityManagerFactory_SRC")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory_SRC() {
		LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
		emFactory.setDataSource(srcDataSource);
		emFactory.setPackagesToScan("com.kbank.eai.domain");
		emFactory.setPersistenceUnitName("SRC");
		emFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emFactory.setJpaProperties(jpaProperties());
		return emFactory;
	}
	
	@Bean
	@Qualifier("EntityManagerFactory_DST")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory_DST() {
		LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
		emFactory.setDataSource(dstDataSource);
		emFactory.setPackagesToScan("com.kbank.eai.domain");
		emFactory.setPersistenceUnitName("DST");
		emFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emFactory.setJpaProperties(jpaProperties());
		return emFactory;
	}
	
	// Define any additional JPA properties if needed
    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "create"); // Set the ddl-auto mode here
//        properties.put("hibernate.show_sql", "true"); // Enable SQL query output to the console
        properties.put("hibernate.format_sql", "true"); // Optional: Format the SQL queries nicely
        properties.put("hibernate.allow_update_outside_transaction", true);
        // Add any other required properties
        return properties;
    }
    
}
