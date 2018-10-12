package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@SuppressWarnings("ALL")
@Component
@Transactional
public class WorkTableProcessor implements TableProcessor<Work> {

    private static final String INSERT =
        "INSERT INTO Work (id, title, appearedBegins, appearedEnds, rangePrecision) "+
          "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE =
        "UPDATE Work SET title = ?, appearedBegins = ?, appearedEnds = ?, rangePrecision = ? WHERE id = ?";

    private static final String DELETE =
        "DELETE FROM Work WHERE id = ?";

    private static final String EXISTS_QUERY =
        "SELECT COUNT(id) FROM Work WHERE id = ?";

    private static final int MAX_CACHE_SIZE = 128;

    private final Set<Long> cache = new ConcurrentSkipListSet<>();

    private JdbcTemplate jdbcTemplate;

    public WorkTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean exists(Work e) {
        if (e == null)
            return false;
        if (cache.contains(e.getId()))
            return true;
        var count = jdbcTemplate.queryForObject(EXISTS_QUERY, (rs, rn) -> rs.getLong(1), e.getId());
        var res = count != null && count == 1;
        if (res && cache.size() < MAX_CACHE_SIZE)
            cache.add(e.getId());
        return res;
    }

    @Override
    public boolean insert(Work e) {
        if (e == null)
            return false;
        if (exists(e))
            return false;
        jdbcTemplate.update(INSERT, e.getId(),
                e.getTitle(),
                e.whenAppeared().getBegins(),
                e.whenAppeared().getEnds(),
                e.whenAppeared().getPrecision().code());
        return true;
    }

    @Override
    public boolean delete(Work e) {
        if (e == null)
            return false;
        if (!exists(e))
            return false;
        cache.remove(e.getId());
        jdbcTemplate.update(DELETE, e.getId());
        return true;
    }

    @Override
    public boolean update(Work e) {
        if (e == null)
            return false;
        if (!exists(e))
            return false;
        jdbcTemplate.update(UPDATE, e.getTitle(),
                e.whenAppeared().getBegins(),
                e.whenAppeared().getEnds(),
                e.whenAppeared().getPrecision().code());
        return true;
    }
}




