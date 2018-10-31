package com.jkojote.library.persistence.bridge;

import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.SubjectTable;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.BridgeTableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("workSubjectBridge")
@Transactional
class WorkSubjectBridgeTableProcessor implements BridgeTableProcessor<Work, Subject> {

    private SubjectTable subjectTable;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public WorkSubjectBridgeTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean removeRecord(Work work, Subject subject) {
        if (!exists(work, subject))
            return false;
        int subjectId = subjectTable.exists(subject);
        String DELETE =
            "DELETE FROM WorkSubject WHERE workId = ? AND subjectId = ?";
        jdbcTemplate.update(DELETE, work.getId(), subjectId);
        return true;
    }

    @Override
    public boolean addRecord(Work work, Subject subject) {
        if (exists(work, subject))
            return false;
        int subjectId = subjectTable.save(subject);
        String INSERT =
            "INSERT INTO WorkSubject (workId, subjectId) VALUES (?, ?)";
        jdbcTemplate.update(INSERT, work.getId(), subjectId);
        return true;
    }

    @Override
    public boolean exists(Work work, Subject subject) {
        String QUERY = "SELECT * FROM WorkSubject WHERE workId = ? AND subjectId = ?";
        int subjectId = subjectTable.exists(subject);
        if (subjectId == -1)
            return false;
        return jdbcTemplate.queryForRowSet(QUERY, work.getId(), subjectId).next();
    }

    @Autowired
    public void setSubjectTable(SubjectTable subjectTable) {
        this.subjectTable = subjectTable;
    }
}
