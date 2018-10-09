package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.mappers.BookMapper;
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
public class BookRepository implements DomainRepository<Book> {

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private JdbcTemplate jdbcTemplate;

    private BookMapper bookMapper;

    private AtomicLong lastId;

    private DomainRepository<Work> workRepository;

    private DomainRepository<BookInstance> bookInstanceRepository;

    private DomainRepository<Publisher> publisherRepository;

    private final Map<Long, Book> cache;

    @Autowired
    public BookRepository(NamedParameterJdbcTemplate namedJdbcTemplate,
                          JdbcTemplate jdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        cache = new ConcurrentHashMap<>();
        initLastId();
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
    public long nextId() {
        return lastId.incrementAndGet();
    }

    @Override
    public boolean exists(Book book) {
        return findById(book.getId()) != null;
    }

    @Override
    public boolean save(Book book) {
        if (exists(book))
            return false;
        final var INSERT =
             "INSERT INTO Book (id, workId, publisherId, edition) " +
               "VALUES (:id, :workId, :publisherId, :edition)";
        var params = new MapSqlParameterSource("id", book.getId())
                .addValue("workId", book.getBasedOn().getId())
                .addValue("publisherId", book.getPublisher().getId())
                .addValue("edition", book.getEdition());
        if (!workRepository.exists(book.getBasedOn()))
            workRepository.save(book.getBasedOn());
        if (!publisherRepository.exists(book.getPublisher()))
            publisherRepository.save(book.getPublisher());
        else
            jdbcTemplate.update(INSERT, book);
        cache.put(book.getId(), book);
        bookInstanceRepository.saveAll(book.getBookInstances());
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

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM Book";
        var rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        lastId = new AtomicLong(rs.getLong(1));
    }
}
