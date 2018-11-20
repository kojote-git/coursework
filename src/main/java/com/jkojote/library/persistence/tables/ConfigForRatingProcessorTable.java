package com.jkojote.library.persistence.tables;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;

@Configuration
@ComponentScan(
    basePackageClasses = RatingTableProcessor.class,
    useDefaultFilters = false,
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        value = RatingTableProcessor.class
    )
)
@DirtiesContext
public class ConfigForRatingProcessorTable {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .addScript("rating.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
