package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.reader.Rating;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.persistence.MapCache;
import com.jkojote.library.persistence.MapCacheImpl;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

@Component("ratingTable")
class RatingTableProcessor implements TableProcessor<Rating> {

    private static final String INSERT =
        "INSERT INTO Rating (bookId, readerId, rating) VALUES (?, ?, ?)";

    private static final String UPDATE =
        "UPDATE Rating SET rating = ? WHERE bookId = ? AND readerId = ?";

    private static final String DELETE =
        "DELETE FROM Rating WHERE bookId = ? AND readerId = ?";

    private static final String EXISTS =
        "SELECT COUNT(*) FROM Rating WHERE bookId = ? AND readerId = ?";

    private final MapCache<Long, Set<Long>> cache;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.cache = new MapCacheImpl<>(128);
        ForkJoinPool.commonPool().execute(this::clearCacheTask);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean exists(Rating e) {
        if (cacheContains(e))
            return true;
        Long count = jdbcTemplate.queryForObject(EXISTS, (rs, rn) -> {
            return rs.getLong(1);
        }, e.getBook().getId(), e.getReader().getId());
        if (count == null || count == 0)
            return false;
        putToCache(e);
        return true;
    }

    @Override
    public boolean insert(Rating e) {
        if (exists(e))
            return false;
        jdbcTemplate.update(INSERT, e.getBook().getId(),
                e.getReader().getId(), e.getRating());
        return true;
    }

    @Override
    public boolean delete(Rating e) {
        if (!exists(e))
            return false;
        removeFromCache(e);
        jdbcTemplate.update(DELETE, e.getBook().getId(), e.getReader().getId());
        return true;
    }

    @Override
    public boolean update(Rating e) {
        if (!exists(e))
            return false;
        jdbcTemplate.update(UPDATE, e.getRating(), e.getBook().getId(), e.getReader().getId());
        return true;
    }

    @Override
    public void batchInsert(Collection<Rating> c) {
        jdbcTemplate.batchUpdate(INSERT, new RatingBatchSetter(c));
    }

    private boolean cacheContains(Rating r) {
        Book book = r.getBook();
        Reader reader = r.getReader();
        if (cache.contains(book.getId())) {
            return cache.get(book.getId()).contains(reader.getId());
        }
        return false;
    }

    private void putToCache(Rating r) {
        Book book = r.getBook();
        Reader reader = r.getReader();
        if (cache.contains(book.getId())) {
            cache.get(book.getId()).add(reader.getId());
        } else {
            Set<Long> ids = new HashSet<>();
            ids.add(reader.getId());
            cache.put(book.getId(), ids);
        }
    }

    private void clearCacheTask() {
        while (true) {
            try {
                Thread.sleep(1000 * 60);
                cache.clean();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeFromCache(Rating rating) {
        if (cacheContains(rating)) {
            cache.get(rating.getBook().getId())
                    .remove(rating.getReader().getId());
        }
    }
}
