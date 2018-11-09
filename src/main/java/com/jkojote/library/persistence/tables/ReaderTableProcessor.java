package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;

@Component("readerTable")
@Transactional
public class ReaderTableProcessor implements TableProcessor<Reader> {

    private Set<Long> idCache;

    private JdbcTemplate jdbcTemplate;

    private static final int CACHE_MAX_SIZE = 256;

    private static final String INSERT =
        "INSERT INTO Reader (id, email, password) VALUES (?, ?, ?)";

    private static final String UPDATE =
        "UPDATE Reader SET email = ?, password = ? WHERE id = ?";

    private static final String REMOVE =
        "DELETE FROM Reader WHERE id = ?";

    private static final String SELECT_BY_ID =
        "SELECT COUNT(*) FROM Reader WHERE id = ?";

    @Autowired
    public ReaderTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        idCache = new ConcurrentSkipListSet<>();
        ForkJoinPool.commonPool().execute(this::clearCacheTask);
    }

    @Override
    public boolean exists(Reader e) {
        if (e == null)
            return false;
        long id = e.getId();
        if (idCache.contains(e.getId())) {
            return true;
        }
        Long count = jdbcTemplate.queryForObject(SELECT_BY_ID, (rs, rn) -> rs.getLong(1), id);
        if (count == null || count == 0)
            return false;
        tryPutToCache(id);
        return true;
    }

    @Override
    public boolean insert(Reader e) {
        if (e == null)
            return false;
        if (exists(e))
            return false;
        jdbcTemplate.update(INSERT, e.getId(),
                e.getEmail().toString(),
                e.getEncryptedPassword());
        return true;
    }

    @Override
    public boolean delete(Reader e) {
        if (!exists(e))
            return false;
        jdbcTemplate.update(REMOVE, e.getId());
        idCache.remove(e.getId());
        return true;
    }

    @Override
    public boolean update(Reader e) {
        if (!exists(e))
            return false;
        jdbcTemplate.update(UPDATE, e.getEmail().toString(),
                e.getEncryptedPassword(),
                e.getId());
        return true;
    }

    private boolean tryPutToCache(long value) {
        if (idCache.size() < CACHE_MAX_SIZE) {
            idCache.add(value);
            return true;
        }
        return false;
    }

    private void clearCacheTask() {
        while (true) {
            try {
                Thread.sleep(10000);
                idCache.clear();
            } catch (InterruptedException e) {

            }
        }
    }
}
