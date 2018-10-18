package com.jkojote.library.persistence.bridge;

import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.SubjectTable;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.BridgeTableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("workSubjectBridge")
@Transactional
public class WorkSubjectBridgeTableProcessor implements BridgeTableProcessor<Work, Subject> {

    private SubjectTable subjectRepository;

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public WorkSubjectBridgeTableProcessor(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public boolean removeRecord(Work work, Subject subject) {
        if (!exists(work, subject))
            return false;
        int subjectId = subjectRepository.exists(subject);
        String DELETE =
            "DELETE FROM WorkSubject WHERE workId = :workId AND subjectId = :subjectId";
        SqlParameterSource params = new MapSqlParameterSource("workId", work.getId())
                .addValue("subjectId", subjectId);
        namedJdbcTemplate.update(DELETE, params);
        return true;
    }

    @Override
    public boolean addRecord(Work work, Subject subject) {
        if (exists(work, subject))
            return false;
        int subjectId = subjectRepository.save(subject);
        String INSERT =
            "INSERT INTO WorkSubject (workId, subjectId) VALUES (:workId, :subjectId)";
        SqlParameterSource params = new MapSqlParameterSource("workId", work.getId())
                .addValue("subjectId", subjectId);
        namedJdbcTemplate.update(INSERT, params);
        return true;
    }

    @Override
    public boolean exists(Work work, Subject subject) {
        String QUERY = "SELECT * FROM WorkSubject WHERE workId = :workId AND subjectId = :subjectId";
        int subjectId = subjectRepository.exists(subject);
        if (subjectId == -1)
            return false;
        SqlParameterSource params = new MapSqlParameterSource("workId", work.getId())
                .addValue("subjectId", subjectId);
        return namedJdbcTemplate.queryForRowSet(QUERY, params).next();
    }

    @Autowired
    public void setSubjectRepository(SubjectTable subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
}
