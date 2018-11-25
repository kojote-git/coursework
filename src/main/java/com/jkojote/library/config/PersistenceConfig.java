package com.jkojote.library.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@ContextConfiguration
@ComponentScan("com.jkojote.library")
@EnableTransactionManagement
public class PersistenceConfig {

    private DataSource dataSource;

    @Bean
    public DataSource dataSource() {
        if (dataSource != null)
            return dataSource;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/library?serverTimezone=UTC");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername("libadm");
        config.setPassword("libadmpass951");
        return dataSource = new HikariDataSource(config);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
