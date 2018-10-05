package com.jkojote.library.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;

@ContextConfiguration
@ComponentScan("com.jkojote.library.persistence")
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
}
