package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.author.AuthorRepository;
import com.jkojote.library.domain.shared.Utils;
import com.jkojote.library.persistence.listeners.AuthorStateListener;
import com.jkojote.library.persistence.mappers.AuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Transactional
public class AuthorRepositoryImpl implements AuthorRepository {

    private final Map<Long, Author> cache = new ConcurrentHashMap<>();

    private AuthorMapper authorMapper;

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private JdbcTemplate jdbcTemplate;

    private CascadeWorkAuthorPersistence cascadePersistence;

    private AuthorStateListener authorStateListener;

    private AtomicLong lastId;

    @Autowired
    public AuthorRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate,
                                 JdbcTemplate jdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        initLastId();
    }

    @Override
    public Author findById(long id) {
        if (cache.containsKey(id))
            return cache.get(id);
        final var query  = "SELECT id, firstName, middleName, lastName FROM Author WHERE id = :id";
        var params = new MapSqlParameterSource("id", id);
        try {
            var author = namedJdbcTemplate.queryForObject(query, params, authorMapper);
            cache.put(id, author);
            return author;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Autowired
    public void setAuthorStateListener(AuthorStateListener authorStateListener) {
        this.authorStateListener = authorStateListener;
    }

    @Autowired
    public void setCascadePersistence(CascadeWorkAuthorPersistence cascadePersistence) {
        this.cascadePersistence = cascadePersistence;
    }

    @Autowired
    public void setAuthorMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    @Override
    public List<Author> findAll() {
        final var query = "SELECT id, firstName, middleName, lastName FROM Author";
        return jdbcTemplate.query(query, authorMapper);
    }

    @Override
    public long nextId() {
        return lastId.incrementAndGet();
    }

    @Override
    public boolean exists(Author author) {
        return findById(author.getId()) != null;
    }

    @Override
    @Transactional
    public boolean save(Author author) {
        if (exists(author))
            return false;
        cascadePersistence.saveAuthor(author);
        return true;
    }

    @Override
    public boolean update(Author author) {
        if (!exists(author))
            return false;
        cascadePersistence.updateAuthor(author);
        return true;
    }

    @Override
    public boolean remove(Author author) {
        if (!exists(author))
            return false;
        var DELETE =
            "DELETE FROM Author WHERE id =:id";
        var params = new MapSqlParameterSource("id", author.getId());
        namedJdbcTemplate.update(DELETE, params);
        cache.remove(author.getId());
        author.removeListener(authorStateListener);
        var works = author.getWorks();
        for (int i = 0; i < works.size(); i++)
            works.get(i).removeAuthor(author);
        return true;
    }

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM Author";
        var rs = namedJdbcTemplate.queryForRowSet(QUERY, Utils.emptyParams());
        rs.next();
        this.lastId = new AtomicLong(rs.getLong(1));
    }
}
