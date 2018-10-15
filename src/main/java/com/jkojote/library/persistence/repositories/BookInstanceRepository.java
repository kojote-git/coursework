package com.jkojote.library.persistence.repositories;

import com.google.common.collect.Iterators;
import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.LazyObject;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Transactional
public class BookInstanceRepository implements DomainRepository<BookInstance> {

    private final Map<Long, BookInstance> cache = new ConcurrentHashMap<>();

    private RowMapper<BookInstance> mapper;

    private JdbcTemplate jdbcTemplate;

    private AtomicLong lastId;

    private TableProcessor<BookInstance> bookInstanceTable;

    @Autowired
    public BookInstanceRepository(JdbcTemplate jdbcTemplate,
                                  NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                  TableProcessor<BookInstance> bookInstanceTable) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookInstanceTable = bookInstanceTable;
        initLastId();
    }

    @Autowired
    public void setMapper(RowMapper<BookInstance> mapper) {
        this.mapper = mapper;
    }

    @Override
    public BookInstance findById(long id) {
        var QUERY =
            "SELECT id, bookId, format, isbn13 FROM BookInstance WHERE id = ?";
        var bookInstance = cache.get(id);
        if (bookInstance != null)
            return bookInstance;
        try {
            bookInstance = jdbcTemplate.queryForObject(QUERY, mapper, id);
            cache.put(id, bookInstance);
            return bookInstance;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public List<BookInstance> findAll() {
        var QUERY = "SELECT id, bookId, format, isbn13 FROM BookInstance";
        return jdbcTemplate.query(QUERY, mapper);
    }

    @Override
    public long nextId() {
        return lastId.incrementAndGet();
    }

    @Override
    public boolean exists(BookInstance entity) {
        return bookInstanceTable.exists(entity);
    }

    @Override
    public boolean save(BookInstance instance) {
        if (exists(instance))
            return false;
        bookInstanceTable.insert(instance);
        cache.put(instance.getId(), instance);
        return true;
    }

    @Override
    public boolean remove(BookInstance instance) {
        if (!exists(instance))
            return false;
        bookInstanceTable.delete(instance);
        instance.getBook().removeBookInstance(instance);
        cache.remove(instance.getId());
        return true;
    }

    @Override
    public boolean update(BookInstance instance) {
        if (!exists(instance))
            return false;
        bookInstanceTable.update(instance);
        return true;
    }

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM BookInstance";
        var rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        lastId = new AtomicLong(rs.getLong(1));
    }
}
