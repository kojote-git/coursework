package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.mappers.AuthorMapper;
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

    private AuthorMapper authorMapper;

    @Autowired
    public LazyAuthorListFetcher(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setAuthorMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public AuthorMapper getAuthorMapper() {
        return authorMapper;
    }

    @Override
    public List<Author> fetchFor(Work work) {
        var params = new MapSqlParameterSource("workId", work.getId());
        return jdbcTemplate.query(QUERY, params, authorMapper);
    }
}
