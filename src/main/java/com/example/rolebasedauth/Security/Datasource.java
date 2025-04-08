package com.example.rolebasedauth.Security;

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

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/role_auth_db");
        dataSource.setUsername("root");
        dataSource.setPassword("root"); // Replace with your actual password

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
