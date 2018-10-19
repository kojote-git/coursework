package com.jkojote.library.persistence.fetchers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;

@Configuration
@ComponentScan(
    basePackageClasses = LazyBookFileFetcher.class,
    useDefaultFilters = false,
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, value = LazyBookFileFetcher.class
))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ForBookInstanceConfig {

    private DataSource dataSource;

    @Bean
    public DataSource dataSource() {
        if (dataSource != null)
            return dataSource;
        return new EmbeddedDatabaseBuilder()
                .addScript("bookinstance.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();

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
