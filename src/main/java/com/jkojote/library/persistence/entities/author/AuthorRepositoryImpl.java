package com.jkojote.library.persistence.entities.author;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.author.AuthorRepository;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.Name;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.internals.lists.LazyWorkList;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Transactional
public class AuthorRepositoryImpl implements AuthorRepository {

    private final Map<Long, Author> cache = new ConcurrentHashMap<>();

    private ListFetcher<Author, Work> fetcher;

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private JdbcTemplate jdbcTemplate;

    private AuthorRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate,
                                 JdbcTemplate jdbcTemplate,
                                 ListFetcher<Author, Work> fetcher) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.fetcher = fetcher;
    }

    @Override
    public Author findById(long id) {
        if (cache.containsKey(id))
            return cache.get(id);
        var query = "SELECT id, firstName, middleName, lastName FROM Author WHERE id = :id";
        var params = new MapSqlParameterSource("id", id);
        try {
            var res = namedJdbcTemplate.queryForObject(query, params, this::mapAuthor);
            cache.put(id, res);
            return res;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public List<Author> findAll() {
        var query = "SELECT id, firstName, middleName, lastName FROM Author";
        return jdbcTemplate.query(query, this::mapAuthor);
    }

    @Override
    public long nextId() {
        return 0;
    }

    @Override
    public boolean exists(Author author) {
        return findById(author.getId()) != null;
    }

    @Override
    public boolean save(Author author) {
        if (exists(author))
            return false;
        return true;
    }

    @Override
    public boolean update(Author author) {
        return false;
    }

    @Override
    public boolean remove(Author author) {
        return false;
    }

    private Author mapAuthor(ResultSet rs, int rn) throws SQLException {
        var id         = rs.getLong("id");
        var firstName  = rs.getString("firstName");
        var middleName = rs.getString("middleName");
        var lastName   = rs.getString("lastName");
        var name       = Name.of(firstName, middleName, lastName);
        var works      = new LazyWorkList(fetcher);
        var author     = Author.restore(id, name, works);
        works.setParentEntity(author);
        works.seal();
        return author;
    }
}
