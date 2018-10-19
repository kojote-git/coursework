package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Component("publisherTable")
@Transactional
@SuppressWarnings("Duplicates")
class PublisherTableProcessor implements TableProcessor<Publisher> {

    private static final String INSERT =
        "INSERT INTO Publisher (id, name) VALUES (?, ?)";

    private static final String UPDATE =
        "UPDATE Publisher SET name = ? WHERE id = ?";

    private static final String DELETE =
        "DELETE FROM Publisher WHERE id = ?";

    private static final String QUERY_EXISTS =
        "SELECT COUNT(*) FROM Publisher WHERE id = ?";

    private static final int CACHE_MAX_SIZE = 512;

    private final Set<Long> cache = new ConcurrentSkipListSet<>();

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PublisherTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean exists(Publisher e) {
        if (e == null)
            return false;
        if (cache.contains(e.getId()))
            return true;
        Long count = jdbcTemplate.queryForObject(QUERY_EXISTS, (rs, rn) -> rs.getLong(1), e.getId());
        boolean res = count != null && count == 1;
        if (res)
            tryPutToCache(e.getId());
        return res;
    }

    @Override
    public boolean insert(Publisher e) {
        if (e == null)
            return false;
        if (exists(e))
            return false;
        tryPutToCache(e.getId());
        jdbcTemplate.update(INSERT, e.getId(), e.getName());
        return true;
    }

    @Override
    public boolean delete(Publisher e) {
        if (!exists(e))
            return false;
        cache.remove(e.getId());
        jdbcTemplate.update(DELETE, e.getId());
        return true;
    }

    private void tryPutToCache(long value) {
        if (cache.size() < CACHE_MAX_SIZE)
            cache.add(value);
    }

    @Override
    public boolean update(Publisher e) {
        if (!exists(e))
            return false;
        jdbcTemplate.update(UPDATE, e.getName(), e.getId());
        return true;
    }
}
