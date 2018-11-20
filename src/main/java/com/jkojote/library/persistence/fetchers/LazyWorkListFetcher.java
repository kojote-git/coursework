package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.ListFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("worksFetcher")
@Transactional
class LazyWorkListFetcher implements ListFetcher<Author, Work> {

    private static final String QUERY =
        "SELECT id, title, lang FROM Work w " +
          "INNER JOIN WorkAuthor wa "+
            "ON wa.workId = w.id "+
        "WHERE wa.authorId = :authorId";

    private NamedParameterJdbcTemplate jdbcTemplate;

    private RowMapper<Work> workMapper;

    @Autowired
    public LazyWorkListFetcher(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public RowMapper<Work> getWorkMapper() {
        return workMapper;
    }

    @Autowired
    @Qualifier("workMapper")
    public void setWorkMapper(RowMapper<Work> workMapper) {
        this.workMapper = workMapper;
    }

    @Override
    public List<Work> fetchFor(Author author) {
        SqlParameterSource params = new MapSqlParameterSource("authorId", author.getId());
        return jdbcTemplate.query(QUERY, params, workMapper);
    }
}
