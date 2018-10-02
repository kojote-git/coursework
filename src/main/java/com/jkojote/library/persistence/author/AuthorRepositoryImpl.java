package com.jkojote.library.persistence.author;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.author.AuthorRepository;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.Name;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.internals.lists.LazyAuthorList;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Transactional
public class AuthorRepositoryImpl implements AuthorRepository {

    private final Map<Long, Author> cache = new ConcurrentHashMap<>();

    private ListFetcher<Author, Work> fetcher;

    private NamedParameterJdbcTemplate jdbcTemplate;

    private AuthorRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate,
                                 ListFetcher<Author, Work> fetcher) {
        this.jdbcTemplate = jdbcTemplate;
        this.fetcher = fetcher;
    }

    @Override
    public Author findById(long id) {
        if (cache.containsKey(id))
            return cache.get(id);
        var query = "SELECT firstName, middleName, lastName FROM Author WHERE id = :id";
        var params = new MapSqlParameterSource("id", id);
        try {
            jdbcTemplate.queryForObject(query, params, (rs, rn) -> {
                Name name = Name.of(
                    rs.getString("firstName"), rs.getString("middleName"),
                    rs.getString("lastName")
                );
                return null;
            });
        } catch (RuntimeException e) {
            return null;
        }
        return null;
    }

    @Override
    public List<Author> findAll() {
        return null;
    }

    @Override
    public boolean exists(Author author) {
        return false;
    }

    @Override
    public boolean save(Author author) {
        return false;
    }

    @Override
    public boolean update(Author author) {
        return false;
    }

    @Override
    public boolean remove(Author author) {
        return false;
    }
}
