package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.SubjectTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Transactional
public class SubjectTableImpl implements SubjectTable {

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private final Map<String, Integer> cache = new ConcurrentHashMap<>();

    @Autowired
    public SubjectTableImpl(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public int exists(Subject subject) {
        Integer subjectId = cache.get(subject.asString());
        if (subjectId == null) {
            String query = "SELECT id FROM Subject WHERE subject = :subject";
            SqlParameterSource params = new MapSqlParameterSource("subject", subject.asString());
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
        int t = exists(subject);
        if (t != -1)
            return t;
        String INSERT =
            "INSERT INTO Subject (subject) VALUES (:subject)";
        SqlParameterSource params = new MapSqlParameterSource("subject", subject.asString());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbcTemplate.update(INSERT, params, keyHolder);
        int value = keyHolder.getKey().intValue();
        cache.put(subject.asString(), value);
        return value;
    }

    @Override
    public void remove(Subject subject) {
        int t = exists(subject);
        if (t == -1)
            return;
        String DELETE = "DELETE FROM Subject WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource("id", t);
        namedJdbcTemplate.update(DELETE, params);
        cache.remove(subject.asString());
    }
}
