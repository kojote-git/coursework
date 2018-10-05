package com.jkojote.library.persistence.internals.fetchers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.entities.mappers.WorkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class LazyWorkListFetcher implements ListFetcher<Author, Work> {

    private static final String QUERY =
        "SELECT id, title, written FROM Work w " +
          "INNER JOIN WorkAuthor wa "+
            "ON wa.workId = w.id "+
        "WHERE wa.authorId = :authorId";

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private WorkMapper workMapper;

    @Autowired
    public LazyWorkListFetcher(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public WorkMapper getWorkMapper() {
        return workMapper;
    }

    @Override
    public List<Work> fetchFor(Author author) {
        var params = new MapSqlParameterSource("authorId", author.getId());
        return jdbcTemplate.query(QUERY, params, workMapper);
    }
}
