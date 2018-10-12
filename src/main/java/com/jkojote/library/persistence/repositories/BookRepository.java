package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Transactional
public class BookRepository implements DomainRepository<Book> {

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private JdbcTemplate jdbcTemplate;

    private RowMapper<Book> bookMapper;

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
    public void setBookMapper(RowMapper<Book> bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Autowired
    public void setWorkRepository(DomainRepository<Work> workRepository) {
        this.workRepository = workRepository;
    }

    @Autowired
    public void setBookInstanceRepository(DomainRepository<BookInstance> bookInstanceRepository) {
        this.bookInstanceRepository = bookInstanceRepository;
    }

    @Autowired
    public void setPublisherRepository(DomainRepository<Publisher> publisherRepository) {
        this.publisherRepository = publisherRepository;
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
        if (!publisherRepository.exists(book.getPublisher())) {
            publisherRepository.save(book.getPublisher());
        }
        if (!workRepository.exists(book.getBasedOn())) {
            workRepository.save(book.getBasedOn());
        }
        cache.put(book.getId(), book);
        var params = new MapSqlParameterSource("id", book.getId())
                .addValue("workId", book.getBasedOn().getId())
                .addValue("publisherId", book.getPublisher().getId())
                .addValue("edition", book.getEdition());
        namedJdbcTemplate.update(INSERT, params);
        bookInstanceRepository.saveAll(book.getBookInstances());
        return true;
    }

    @Override
    public boolean update(Book book) {
        if (!exists(book))
            return false;
        var UPDATE =
            "UPDATE Book SET workId = ?, publisherId = ?, edition = ? WHERE id = ?";
        jdbcTemplate
            .update(UPDATE, book.getBasedOn().getId(),
                    book.getPublisher().getId(),
                    book.getEdition(),
                    book.getId()
            );
        return true;
    }

    @Override
    public boolean remove(Book book) {
        if (!exists(book))
            return false;
        var DELETE =
            "DELETE FROM Book WHERE id = ?";
        jdbcTemplate.update(DELETE, book.getId());
        cache.remove(book.getId());
        for (var inst : book.getBookInstances())
            bookInstanceRepository.remove(inst);
        return true;
    }

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM Book";
        var rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        lastId = new AtomicLong(rs.getLong(1));
    }

}
