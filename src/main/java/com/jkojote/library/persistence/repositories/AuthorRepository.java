package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.Utils;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.TableProcessor;
import com.jkojote.library.persistence.listeners.AuthorStateListener;
import com.jkojote.library.persistence.mappers.AuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("authorRepository")
@Transactional
public class AuthorRepository implements DomainRepository<Author> {

    private final Map<Long, Author> cache = new ConcurrentHashMap<>();

    private RowMapper<Author> authorMapper;

    private JdbcTemplate jdbcTemplate;

    private CascadeWorkAuthorPersistence cascadePersistence;

    private TableProcessor<Author> authorTable;

    private DomainEventListener<Author> authorStateListener;

    private AtomicLong lastId;

    @Autowired
    public AuthorRepository(@Qualifier("authorTable")
                            TableProcessor<Author> authorTable,
                            JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.authorTable = authorTable;
        initLastId();
    }

    @Autowired
    @Qualifier("authorMapper")
    public void setAuthorMapper(RowMapper<Author> authorMapper) {
        this.authorMapper = authorMapper;
    }

    @Autowired
    public void setCascadePersistence(CascadeWorkAuthorPersistence cascadePersistence) {
        this.cascadePersistence = cascadePersistence;
    }

    @Autowired
    @Qualifier("authorStateListener")
    public void setAuthorStateListener(DomainEventListener<Author> authorStateListener) {
        this.authorStateListener = authorStateListener;
    }

    @Override
    public Author findById(long id) {
        if (cache.containsKey(id))
            return cache.get(id);
        final String QUERY =
            "SELECT id, firstName, middleName, lastName FROM Author WHERE id = ?";
        try {
            Author author = jdbcTemplate.queryForObject(QUERY, authorMapper, id);
            cache.put(id, author);
            return author;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public List<Author> findAll() {
        final String query = "SELECT id, firstName, middleName, lastName FROM Author";
        return jdbcTemplate.query(query, authorMapper);
    }

    @Override
    public long nextId() {
        return lastId.incrementAndGet();
    }

    @Override
    public boolean exists(Author author) {
        return authorTable.exists(author);
    }

    @Override
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
        authorTable.delete(author);
        cache.remove(author.getId());
        author.removeListener(authorStateListener);
        List<Work> works = author.getWorks();
        for (int i = 0; i < works.size(); i++)
            works.get(i).removeAuthor(author);
        return true;
    }

    private void initLastId() {
        String QUERY = "SELECT MAX(id) FROM Author";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        this.lastId = new AtomicLong(rs.getLong(1));
    }
}
