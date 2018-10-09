package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.mappers.PublisherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
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

    private PublisherMapper publisherMapper;

    private AtomicLong lastId;

    public PublisherRepository(JdbcTemplate jdbcTemplate,
                               NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedParameterJdbcTemplate;
        cache = new ConcurrentHashMap<>();
        initLastId();
    }


    @Autowired
    public void setPublisherMapper(PublisherMapper publisherMapper) {
        this.publisherMapper = publisherMapper;
    }

    @Override
    public Publisher findById(long id) {
        var publisher = cache.get(id);
        if (publisher != null)
            return publisher;
        var QUERY =
            "SELECT id, name FROM Publisher";
        return null;
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
        return false;
    }

    @Override
    public boolean remove(Publisher publisher) {
        return false;
    }

    @Override
    public boolean update(Publisher publisher) {
        return false;
    }

    @Override
    public boolean exists(Publisher publisher) {
        return false;
    }

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM Publisher";
        var rs = jdbcTemplate.queryForRowSet(QUERY);
        rs.next();
        lastId = new AtomicLong(rs.getLong(1));
    }
}
