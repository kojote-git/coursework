package com.jkojote.library.persistence.bridge;

import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.SubjectRepository;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.model.work.WorkRepository;
import com.jkojote.library.persistence.BridgeTableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class WorkSubjectBridgeTableProcessor implements BridgeTableProcessor<Work, Subject> {

    private SubjectRepository subjectRepository;

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public WorkSubjectBridgeTableProcessor(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public boolean removeRecord(Work work, Subject subject) {
        if (!exists(work, subject))
            return false;
        var subjectId = subjectRepository.exists(subject);
        var DELETE =
            "DELETE FROM WorkSubject WHERE workId = :workId AND subjectId = :subjectId";
        var params = new MapSqlParameterSource("workId", work.getId())
                .addValue("subjectId", subjectId);
        namedJdbcTemplate.update(DELETE, params);
        return true;
    }

    @Override
    public boolean addRecord(Work work, Subject subject) {
        if (exists(work, subject))
            return false;
        var subjectId = subjectRepository.save(subject);
        var INSERT =
            "INSERT INTO WorkSubject (workId, subjectId) VALUES (:workId, :subjectId)";
        var params = new MapSqlParameterSource("workId", work.getId())
                .addValue("subjectId", subjectId);
        namedJdbcTemplate.update(INSERT, params);
        return true;
    }

    @Override
    public boolean exists(Work work, Subject subject) {
        var QUERY = "SELECT * FROM WorkSubject WHERE workId = :workId AND subjectId = :subjectId";
        var subjectId = subjectRepository.exists(subject);
        if (subjectId == -1)
            return false;
        var params = new MapSqlParameterSource("workId", work.getId())
                .addValue("subjectId", subjectId);
        return namedJdbcTemplate.queryForRowSet(QUERY, params).next();
    }

    @Autowired
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
}
