package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.shared.domain.DomainRepository;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository("publisherRepository")
@Transactional
class PublisherRepository implements DomainRepository<Publisher> {

    private final MapCache<Long, Publisher> cache;

    private JdbcTemplate jdbcTemplate;

    private RowMapper<Publisher> publisherMapper;

    private DomainRepository<Book> bookRepository;

    private TableProcessor<Publisher> publisherTable;

    private static final int UNKNOWN_PUBLISHER_ID = 0;

    private AtomicLong lastId;

    public PublisherRepository(JdbcTemplate jdbcTemplate,
                               @Qualifier("publisherTable")
                               TableProcessor<Publisher> publisherTableProcessor) {
        this.jdbcTemplate = jdbcTemplate;
        cache = new MapCacheImpl<>();
        cache.disable();
        this.publisherTable = publisherTableProcessor;
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
        Publisher publisher = cache.get(id);
        if (publisher != null)
            return publisher;
        String QUERY =
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
        cache.put(publisher.getId(), publisher);
        publisherTable.insert(publisher);
        bookRepository.saveAll(publisher.getBooks());
        return true;
    }

    @Override
    public boolean remove(Publisher publisher) {
        if (publisher.getId() == UNKNOWN_PUBLISHER_ID)
            return false;
        if (!exists(publisher))
            return false;
        String DELETE =
            "DELETE FROM Publisher WHERE id = ?";
        jdbcTemplate.update(DELETE, publisher.getId());
        cache.remove(publisher.getId());
        return true;
    }

    @Override
    public boolean update(Publisher publisher) {
        if (publisher.getId() == UNKNOWN_PUBLISHER_ID)
            return false;
        if (!exists(publisher))
            return false;
        publisherTable.update(publisher);
        return true;
    }

    @Override
    public boolean exists(Publisher publisher) {
        return publisherTable.exists(publisher);
    }

    private void initLastId() {
        String QUERY = "SELECT MAX(id) FROM Publisher";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        lastId = new AtomicLong(rs.getLong(1));
    }
}
