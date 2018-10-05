package com.jkojote.library.persistence.internals.fetchers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.entities.mappers.AuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class LazyAuthorListFetcher implements ListFetcher<Work, Author> {

    private static final String QUERY =
      "SELECT id, firstName, lastName, middleName FROM Author a "+
      "INNER JOIN WorkAuthor wa ON a.id = wa.authorId "+
      "WHERE wa.workId = :workId";

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    public LazyAuthorListFetcher(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AuthorMapper getAuthorMapper() {
        return authorMapper;
    }

    @Override
    public List<Author> fetchFor(Work work) {
        var params = new MapSqlParameterSource("id", work.getId());
        return jdbcTemplate.query(QUERY, params, authorMapper);
    }
}
