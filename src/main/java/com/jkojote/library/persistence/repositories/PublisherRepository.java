package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.mappers.PublisherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Transactional
public class PublisherRepository implements DomainRepository<Publisher> {

    private final Map<Long, Publisher> cache;

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private RowMapper<Publisher> publisherMapper;

    private DomainRepository<Book> bookRepository;

    private AtomicLong lastId;

    public PublisherRepository(JdbcTemplate jdbcTemplate,
                               NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedParameterJdbcTemplate;
        cache = new ConcurrentHashMap<>();
        initLastId();
    }

    @Autowired
    public void setPublisherMapper(RowMapper<Publisher> publisherMapper) {
        this.publisherMapper = publisherMapper;
    }

    @Autowired
    public void setBookRepository(DomainRepository<Book> bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Publisher findById(long id) {
        var publisher = cache.get(id);
        if (publisher != null)
            return publisher;
        var QUERY =
            "SELECT id, name FROM Publisher WHERE id = ?";
        try {
            publisher = jdbcTemplate.queryForObject(QUERY, publisherMapper, id);
            cache.put(id, publisher);
            return publisher;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public List<Publisher> findAll() {
        return jdbcTemplate.query("SELECT * FROM Publisher", publisherMapper);
    }

    @Override
    public long nextId() {
        return lastId.incrementAndGet();
    }

    @Override
    public boolean save(Publisher publisher) {
        if (exists(publisher))
            return false;
        var INSERT =
            "INSERT INTO Publisher (id, name) VALUES (?, ?)";
        jdbcTemplate.update(INSERT, publisher.getId(), publisher.getName());
        cache.put(publisher.getId(), publisher);
        bookRepository.saveAll(publisher.getBooks());
        return true;
    }

    @Override
    public boolean remove(Publisher publisher) {
        if (!exists(publisher))
            return false;
        var DELETE =
            "DELETE FROM Publisher WHERE id = ?";
        jdbcTemplate.update(DELETE, publisher.getId());
        return true;
    }

    @Override
    public boolean update(Publisher publisher) {
        if (!exists(publisher))
            return false;
        var UPDATE =
            "UPDATE Publisher SET name = ? WHERE id = ?";
        jdbcTemplate.update(UPDATE, publisher.getName(), publisher.getId());
        return true;
    }

    @Override
    public boolean exists(Publisher publisher) {
        return findById(publisher.getId()) != null;
    }

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM Publisher";
        var rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        lastId = new AtomicLong(rs.getLong(1));
    }
}
