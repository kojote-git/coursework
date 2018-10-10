package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.Utils;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.listeners.WorkStateListener;
import com.jkojote.library.persistence.mappers.WorkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
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
public class WorkRepository implements DomainRepository<Work> {

    private final Map<Long, Work> cache = new ConcurrentHashMap<>();

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private CascadeWorkAuthorPersistence cascadePersistence;

    private RowMapper<Work> workMapper;

    private DomainEventListener<Work> workStateListener;

    private AtomicLong lastId;

    @Autowired
    public WorkRepository(RowMapper<Work> workMapper,
                          NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.workMapper = workMapper;
        this.namedJdbcTemplate = namedJdbcTemplate;
        initLastId();
    }

    @Autowired
    public void setWorkStateListener(DomainEventListener<Work> workStateListener) {
        this.workStateListener = workStateListener;
    }

    @Autowired
    public void setCascadePersistence(CascadeWorkAuthorPersistence cascadePersistence) {
        this.cascadePersistence = cascadePersistence;
    }

    @Override
    public Work findById(long id) {
        var work = cache.get(id);
        if (work != null)
            return work;
        var QUERY =
            "SELECT * FROM Work WHERE id = :id";
        try {
            var params = new MapSqlParameterSource("id", id);
            work = namedJdbcTemplate.queryForObject(QUERY, params, workMapper);
            cache.put(id, work);
            return work;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public List<Work> findAll() {
        var QUERY = "SELECT * FROM Work";
        return namedJdbcTemplate.query(QUERY, Utils.emptyParams(), workMapper);
    }

    @Override
    public boolean exists(Work work) {
        return findById(work.getId()) != null;
    }

    @Override
    public long nextId() {
        return lastId.incrementAndGet();
    }

    @Override
    public boolean save(Work work) {
        if (exists(work))
            return false;
        cascadePersistence.saveWork(work);
        work.addEventListener(workStateListener);
        return true;
    }

    @Override
    public boolean update(Work work) {
        if (!exists(work))
            return false;
        cascadePersistence.updateWork(work);
        return true;
    }

    @Override
    public boolean remove(Work work) {
        if (!exists(work))
            return false;
        var DELETE = "DELETE FROM Work WHERE id = :id";
        var params = new MapSqlParameterSource("id", work.getId());
        namedJdbcTemplate.update(DELETE, params);
        work.removeListener(workStateListener);
        var authors = work.getAuthors();
        for (int i = 0; i < authors.size(); )
            authors.get(i).removeWork(work);
        return true;
    }

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM Work";
        var res = namedJdbcTemplate.queryForRowSet(QUERY, Utils.emptyParams());
        res.next();
        this.lastId = new AtomicLong(res.getLong(1));
    }
}
