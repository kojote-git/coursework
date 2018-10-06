package com.jkojote.library.persistence.entities;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.author.AuthorRepository;
import com.jkojote.library.domain.shared.Utils;
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
    public void setCascadePersistence(CascadeWorkAuthorPersistence cascadePersistence) {
        this.cascadePersistence = cascadePersistence;
    }

    private AuthorRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate,
                                 JdbcTemplate jdbcTemplate,
                                 AuthorMapper authorMapper,
                                 AuthorStateListener authorStateListener) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.authorMapper = authorMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.authorStateListener = authorStateListener;
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
        author.addEventListener(authorStateListener);
        return true;
    }

    @Override
    public boolean update(Author author) {
        if (!exists(author))
            return false;
        var UPDATE =
            "UPDATE Author SET " +
                "firstName = :firstName, middleName = :middleName, "+
                "lastName = :lastName WHERE id = :id";
        var params = Utils.paramsForAuthor(author);
        namedJdbcTemplate.update(UPDATE, params);
        return true;
    }

    @Override
    public boolean remove(Author author) {
        if (!exists(author))
            return false;
        var DELETE =
            "DELETE FROM Author WHERE id =: id";
        var params = new MapSqlParameterSource("id", author.getId());
        namedJdbcTemplate.update(DELETE, params);
        cache.remove(author.getId());
        author.removeListener(authorStateListener);
        return true;
    }

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM Author";
        var rs = namedJdbcTemplate.queryForRowSet(QUERY, Utils.emptyParams());
        rs.next();
        this.lastId = new AtomicLong(rs.getLong(1));
    }
}
