package com.jkojote.library.persistence.entities;

import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.model.work.WorkRepository;
import com.jkojote.library.domain.shared.Utils;
import com.jkojote.library.persistence.mappers.WorkMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WorkRepositoryImpl implements WorkRepository {

    private final Map<Long, Work> cache = new ConcurrentHashMap<>();

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private CascadeWorkAuthorPersistence cascadePersistence;

    private WorkMapper workMapper;

    private WorkStateListener workStateListener;

    private AtomicLong lastId;

    @Autowired
    public WorkRepositoryImpl(WorkMapper workMapper,
                              NamedParameterJdbcTemplate namedJdbcTemplate,
                              WorkStateListener workStateListener) {
        this.workMapper = workMapper;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.workStateListener = workStateListener;
        initLastId();
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
        var UPDATE =
            "UPDATE Work SET " +
               "title = :title, appearedBegins = :appearedBegins, "+
               "appearedEnds = :appearedEnds, rangePrecision = :rangePrecision " +
               "WHERE id = :id";
        var params = Utils.paramsForWork(work);
        namedJdbcTemplate.update(UPDATE, params);
        return true;
    }

    @Override
    public boolean delete(Work work) {
        if (!exists(work))
            return false;
        var DELETE = "DELETE FROM Work WHERE id = :id";
        var params = new MapSqlParameterSource("id", work.getId());
        namedJdbcTemplate.update(DELETE, params);
        work.removeListener(workStateListener);
        return true;
    }

    private void initLastId() {
        var QUERY = "SELECT MAX(id) FROM Work";
        var res = namedJdbcTemplate.queryForRowSet(QUERY, Utils.emptyParams());
        res.next();
        this.lastId = new AtomicLong(res.getLong(1));
    }
}
