package com.jkojote.library.persistence.repositories;

import com.jkojote.library.clauses.SqlClause;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.*;
import com.jkojote.library.persistence.MapCache;
import com.jkojote.library.persistence.MapCacheImpl;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

@Repository("authorRepository")
@Transactional
class AuthorRepository implements FilteringAndSortingRepository<Author>, PageableRepository<Author> {

    private final MapCache<Long, Author> cache;

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
        cache = new MapCacheImpl<>(512);
        cache.disable();
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
        if (cache.contains(id))
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
        for (int i = 0; i < works.size(); ) {
            works.get(i).removeAuthor(author);
            author.removeWork(works.get(i));
        }
        return true;
    }

    private void initLastId() {
        String QUERY = "SELECT MAX(id) FROM Author";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        this.lastId = new AtomicLong(rs.getLong(1));
    }

    @Override
    public List<Author> findAll(SqlClause clause) {
        return jdbcTemplate.query("SELECT * FROM Author " + clause.asString(), authorMapper);
    }

    @Override
    public List<Author> findAll(int page, int pageSize) {
        if (page <= 0 || pageSize < 0)
            throw new IllegalArgumentException("page and pageSize must be positive numbers");
        return jdbcTemplate.query("SELECT * FROM Author LIMIT ? OFFSET ?", authorMapper,
                pageSize, (page - 1) * pageSize);
    }

    @Override
    public List<Author> findAll(SqlPageSpecification specification) {
        SqlClause predicate = specification.predicate();
        int page = specification.page();
        int pageSize = specification.pageSize();
        String query = "SELECT * FROM Author " + predicate.asString() + " LIMIT ? OFFSET ?";
        return jdbcTemplate.query(query, authorMapper, pageSize, (page - 1) * pageSize);
    }

    @Override
    public List<Author> findAll(int page, int pageSize, Predicate<Author> predicate) {
        if (page <= 0 || pageSize < 0)
            throw new IllegalArgumentException("page and pageSize must be positive numbers");
        int offset = (page - 1) * pageSize;
        int limit = offset + pageSize;
        List<Author> res = findAll(predicate);
        if (offset > res.size() - 1)
            return Collections.emptyList();
        if (limit > res.size() - 1)
            limit = res.size() - 1;
        return res.subList(offset, limit);
    }
}
