package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.reader.Download;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component("downloadTable")
@Transactional
public class DownloadTableProcessor implements TableProcessor<Download> {

    private static final int MAX_CACHE_SIZE = 256;

    private static final String INSERT =
        "INSERT INTO Download (readerId, bookInstanceId, readerRating) " +
            "VALUES (?, ?, ?)";

    private static final String UPDATE =
        "UPDATE Download SET readerRating = ? WHERE readerId = ? AND bookInstanceId = ?";

    private static final String REMOVE =
        "DELETE FROM Download WHERE readerId = ? AND bookInstanceId = ?";

    private static final String SELECT_COUNT =
        "SELECT COUNT(*) FROM Download WHERE readerId = ? AND bookInstanceId = ?";

    private JdbcTemplate jdbcTemplate;

    private Map<Long, Set<Long>> cache;


    @Autowired
    public DownloadTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.cache = new HashMap<>();
    }

    @Override
    public boolean exists(Download e) {
        if (e == null)
            return false;
        long readerId = e.getReader().getId(),
             bookInstanceId = e.getInstance().getId();
        if (cacheContains(readerId, bookInstanceId))
            return true;
        Long count = jdbcTemplate.queryForObject(SELECT_COUNT, (rs, rn) -> {
            return rs.getLong(1);
        }, readerId, bookInstanceId);
        if (count == null || count == 0)
            return false;
        tryPutCache(e);
        return true;
    }

    @Override
    public boolean insert(Download e) {
        if (e == null || exists(e))
            return false;
        jdbcTemplate.update(INSERT,
                e.getReader().getId(),
                e.getInstance().getId(),
                e.getReaderRating());
        return true;
    }

    @Override
    public boolean delete(Download e) {
        if (!exists(e))
            return false;
        jdbcTemplate.update(REMOVE,
                e.getReader().getId(),
                e.getInstance().getId());
        return true;
    }

    @Override
    public boolean update(Download e) {
        if (!exists(e))
            return false;
        jdbcTemplate.update(UPDATE,
                e.getReaderRating(),
                e.getReader().getId(),
                e.getInstance().getId());
        return true;
    }

    private boolean cacheContains(long readerId, long bookInstanceId) {
        if (cache.containsKey(readerId)) {
            return cache.get(readerId).contains(bookInstanceId);
        }
        return false;
    }

    private boolean tryPutCache(Download d) {
        if (cache.size() < MAX_CACHE_SIZE) {
            long readerId = d.getReader().getId();
            long instanceId = d.getInstance().getId();
            if (cache.containsKey(readerId)) {
                cache.get(readerId).add(instanceId);
            } else {
                Set<Long> set = new HashSet<>();
                set.add(instanceId);
                cache.put(readerId, set);
            }
            return true;
        }
        return false;
    }
}
