package com.jkojote.library.persistence.entities;

import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Transactional
public class SubjectRepositoryImpl implements SubjectRepository {

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private final Map<String, Integer> cache = new ConcurrentHashMap<>();

    @Autowired
    public SubjectRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public int exists(Subject subject) {
        Integer subjectId = cache.get(subject.asString());
        if (subjectId == null) {
            var query = "SELECT id FROM Subject WHERE subject = :subject";
            var params = new MapSqlParameterSource("subject", subject.asString());
            try {
                subjectId = namedJdbcTemplate.queryForObject(query, params, (rs, rn) -> rs.getInt("id"));
            } catch (RuntimeException e) {
                return -1;
            }
        }
        return subjectId;
    }

    @Override
    public int save(Subject subject) {
        var t = exists(subject);
        if (t != -1)
            return t;
        var INSERT =
            "INSERT INTO Subject (subject) VALUES (:subject)";
        var params = new MapSqlParameterSource("subject", subject.asString());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbcTemplate.update(INSERT, params, keyHolder);
        var value = keyHolder.getKey().intValue();
        cache.put(subject.asString(), value);
        return value;
    }

    @Override
    public void remove(Subject subject) {
        var t = exists(subject);
        if (t == -1)
            return;
        var DELETE = "DELETE FROM Subject WHERE id = :id";
        var params = new MapSqlParameterSource("id", t);
        namedJdbcTemplate.update(DELETE, params);
        cache.remove(t);
    }
}
