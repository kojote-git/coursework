package com.jkojote.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.jkojote.library")
public class TestConfig {

    private DataSource dataSource;

    @Bean
    public DataSource dataSource() {
        if (dataSource != null)
            return dataSource;
        return dataSource = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test_data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }
}
