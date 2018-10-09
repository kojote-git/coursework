package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.BookRepository;
import com.jkojote.library.persistence.mappers.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Transactional
public class BookRepositoryImpl implements BookRepository {

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private JdbcTemplate jdbcTemplate;

    private BookMapper bookMapper;

    private final Map<Long, Book> cache;

    @Autowired
    public BookRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate,
                              JdbcTemplate jdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        cache = new ConcurrentHashMap<>();
    }

    @Autowired
    public void setBookMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Override
    public Book findById(long id) {
        var book = cache.get(id);
        if (book != null)
            return book;
        final var QUERY =
            "SELECT * FROM Book WHERE id = ?";
        try {
            book = jdbcTemplate.queryForObject(QUERY, bookMapper, id);
            cache.put(id, book);
            return book;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public List<Book> findAll() {
        return jdbcTemplate.query("SELECT * FROM Book", bookMapper);
    }

    @Override
    public boolean exists(Book book) {
        var QUERY = "SELECT id FROM Book WHERE id = ?";
        return jdbcTemplate.queryForRowSet(QUERY, book.getId()).next();
    }

    @Override
    public boolean save(Book book) {
        if (exists(book))
            return false;
        return true;
    }

    @Override
    public boolean update(Book book) {
        if (!exists(book))
            return false;
        return true;
    }

    @Override
    public boolean remove(Book book) {
        if (!exists(book))
            return false;
        return true;
    }
}
