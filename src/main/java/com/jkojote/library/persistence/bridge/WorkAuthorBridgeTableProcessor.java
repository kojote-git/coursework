package com.jkojote.library.persistence.bridge;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.BridgeTableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("workAuthorBridge")
@Transactional
class WorkAuthorBridgeTableProcessor implements BridgeTableProcessor<Work, Author> {

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public WorkAuthorBridgeTableProcessor(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public boolean removeRecord(Work work, Author author) {
        if (!exists(work, author))
            return false;
        String DELETE =
            "DELETE FROM WorkAuthor WHERE workId = :workId AND authorId = :authorId";
        SqlParameterSource params = new MapSqlParameterSource("workId", work.getId())
                .addValue("authorId", author.getId());
        namedJdbcTemplate.update(DELETE, params);
        return true;
    }

    @Override
    public boolean addRecord(Work work, Author author) {
        if (exists(work, author))
            return false;
        String INSERT =
            "INSERT INTO WorkAuthor (workId, authorId) " +
              "VALUES (:workId, :authorId)";
        SqlParameterSource params = new MapSqlParameterSource("workId", work.getId())
                .addValue("authorId", author.getId());
        namedJdbcTemplate.update(INSERT, params);
        return true;
    }

    @Override
    public boolean exists(Work work, Author author) {
        String QUERY = "SELECT * FROM WorkAuthor WHERE workId = :workId AND authorId = :authorId";
        SqlParameterSource params = new MapSqlParameterSource("workId", work.getId())
                .addValue("authorId", author.getId());
        SqlRowSet rs = namedJdbcTemplate.queryForRowSet(QUERY, params);
        return rs.next();
    }
}
