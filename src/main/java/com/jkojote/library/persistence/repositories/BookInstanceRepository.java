package com.jkojote.library.persistence.repositories;

import com.google.common.collect.Iterators;
import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.LazyObject;
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

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private DomainRepository<Book> bookRepository;

    private AtomicLong lastId;

    @Autowired
    public BookInstanceRepository(JdbcTemplate jdbcTemplate,
                                  NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedParameterJdbcTemplate;
        initLastId();
    }

    @Autowired
    public void setMapper(RowMapper<BookInstance> mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setBookRepository(DomainRepository<Book> bookRepository) {
        this.bookRepository = bookRepository;
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
        return findById(entity.getId()) != null;
    }

    @Override
    public boolean save(BookInstance instance) {
        if (exists(instance))
            return false;
        var INSERT =
            "INSERT INTO BookInstance (id, bookId, format, isbn13, file) " +
            "VALUES (:id, :bookId, :format, :isbn13, :file)";
        var params = new MapSqlParameterSource("id", instance.getId())
                .addValue("bookId", instance.getBook().getId())
                .addValue("format", instance.getFormat().asString())
                .addValue("isbn13", instance.getIsbn13().asString())
                .addValue("file", instance.getFile().asBlob());
        cache.put(instance.getId(), instance);
        namedJdbcTemplate.update(INSERT, params);
        return true;
    }

    @Override
    public boolean remove(BookInstance instance) {
        if (!exists(instance))
            return false;
        var DELETE = "DELETE FROM BookInstance WHERE id = ?";
        jdbcTemplate.update(DELETE, instance.getId());
        cache.remove(instance.getId());
        return true;
    }

    @Override
    public boolean update(BookInstance entity) {
        if (!exists(entity))
            return false;
        var UPDATE = "UPDATE BookInstance SET id = :id, isbn13 = :isbn13, format = :format";
        var file = entity.getFile();
        var isFetched = file instanceof LazyObject && ((LazyObject) file).isFetched();
        var params = new MapSqlParameterSource("id", entity.getId())
                .addValue("isbn13", entity.getIsbn13().asString())
                .addValue("format", entity.getFormat().asString())
                .addValue("bookId", entity.getBook().getId());
        if (isFetched) {
            UPDATE += ", file = :file WHERE id = :id";
            params.addValue("file", entity.getFile().asBlob());
        } else {
            UPDATE += "WHERE id = :id";
        }
        namedJdbcTemplate.update(UPDATE, params);
        return true;
    }

    @Override
    public void saveAll(Collection<BookInstance> instances) {
        var INSERT = BookInstancesBatchSetter.STATEMENT;
        var copy = new HashSet<>(instances);
        for (var instance : copy) {
            if (!bookRepository.exists(instance.getBook()))
                copy.remove(instance);
        }
        jdbcTemplate.batchUpdate(INSERT, new BookInstancesBatchSetter(copy));
    }

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM BookInstance";
        var rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        lastId = new AtomicLong(rs.getLong(1));
    }
}
