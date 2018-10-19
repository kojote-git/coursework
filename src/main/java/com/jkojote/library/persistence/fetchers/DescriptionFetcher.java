package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.LazyObjectFetcher;
import com.jkojote.library.values.OrdinaryText;
import com.jkojote.library.values.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("descriptionFetcher")
@Transactional
class DescriptionFetcher implements LazyObjectFetcher<Work, Text> {

    private static final String QUERY = "SELECT description FROM Work WHERE id = ?";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DescriptionFetcher(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Text fetchFor(Work work) {
        String description = jdbcTemplate.queryForObject(QUERY, (rs, rn) -> {
            return rs.getString("description");
        }, work.getId());
        return OrdinaryText.of(description);
    }
}
