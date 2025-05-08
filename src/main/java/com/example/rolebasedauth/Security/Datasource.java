package com.example.rolebasedauth.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class Datasource {
    
    private static final Logger logger = LoggerFactory.getLogger(Datasource.class);
    
    @Value("${spring.datasource.url}")
    private String url;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        
        // Test connection
        try (Connection conn = dataSource.getConnection()) {
            logger.info("Database connection successful!");
            logger.info("Connected to: {}", conn.getMetaData().getURL());
        } catch (Exception e) {
            logger.error("Database connection failed!", e);
        }
        
        return dataSource;
    }
}