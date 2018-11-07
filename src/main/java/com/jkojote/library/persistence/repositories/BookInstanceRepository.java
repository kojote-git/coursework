package com.jkojote.library.persistence.repositories;

import com.jkojote.library.clauses.SqlClause;
import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.domain.shared.domain.FilteringAndSortingRepository;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("bookInstanceRepository")
@Transactional
@SuppressWarnings("Duplicates")
class BookInstanceRepository implements FilteringAndSortingRepository<BookInstance> {

    private final MapCache<Long, BookInstance> cache;

    private RowMapper<BookInstance> mapper;

    private JdbcTemplate jdbcTemplate;

    private AtomicLong lastId;

    private TableProcessor<BookInstance> bookInstanceTable;

    private DomainRepository<Book> bookRepository;

    @Autowired
    public BookInstanceRepository(JdbcTemplate jdbcTemplate,
                                  @Qualifier("bookInstanceTable")
                                  TableProcessor<BookInstance> bookInstanceTable,
                                  @Qualifier("bookRepository")
                                  DomainRepository<Book> bookRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookInstanceTable = bookInstanceTable;
        this.bookRepository = bookRepository;
        this.cache = new MapCacheImpl<>(512);
        this.cache.disable();
        initLastId();
    }

    @Autowired
    @Qualifier("bookInstanceMapper")
    public void setMapper(RowMapper<BookInstance> mapper) {
        this.mapper = mapper;
    }

    @Override
    public BookInstance findById(long id) {
        String QUERY =
            "SELECT id, bookId, format, isbn13 FROM BookInstance WHERE id = ?";
        BookInstance bookInstance = cache.get(id);
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
        String QUERY = "SELECT id, bookId, format, isbn13 FROM BookInstance";
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
        String QUERY = "SELECT MAX(id) FROM BookInstance";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        lastId = new AtomicLong(rs.getLong(1));
    }

    @Override
    public void saveAll(Collection<BookInstance> entities) {
        String INSERT = BookInstancesBatchSetter.STATEMENT;
        Collection<BookInstance> copy = new HashSet<>(entities);
        for (BookInstance instance : copy) {
            if (!bookRepository.exists(instance.getBook()))
                copy.remove(instance);
        }
        jdbcTemplate.batchUpdate(INSERT, new BookInstancesBatchSetter(copy));
    }

    @Override
    public List<BookInstance> findAll(SqlClause clause) {
        String all = "SELECT id, bookId, format, isbn13 FROM BookInstance ";
        return jdbcTemplate.query(all + clause.asString(), mapper);
    }
}
