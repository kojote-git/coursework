package com.jkojote.library.persistence.bridge;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.BridgeTableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class WorkAuthorBridgeTableProcessor implements BridgeTableProcessor<Work, Author> {

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public WorkAuthorBridgeTableProcessor(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public boolean removeRecord(Work work, Author author) {
        if (!exists(work, author))
            return false;
        var DELETE =
            "DELETE FROM WorkAuthor WHERE workId = :workId AND authorId = :authorId";
        var params = new MapSqlParameterSource("workId", work.getId())
                .addValue("authorId", author.getId());
        namedJdbcTemplate.update(DELETE, params);
        return true;
    }

    @Override
    public boolean addRecord(Work work, Author author) {
        if (exists(work, author))
            return false;
        var INSERT =
            "INSERT INTO WorkAuthor (workId, authorId) " +
              "VALUES (:workId, :authorId)";
        var params = new MapSqlParameterSource("workId", work.getId())
                .addValue("authorId", author.getId());
        namedJdbcTemplate.update(INSERT, params);
        return true;
    }

    @Override
    public boolean exists(Work work, Author author) {
        var QUERY = "SELECT * FROM WorkAuthor WHERE workId = :workId AND authorId = :authorId";
        var params = new MapSqlParameterSource("workId", work.getId())
                .addValue("authorId", author.getId());
        var rs = namedJdbcTemplate.queryForRowSet(QUERY, params);
        return rs.next();
    }
}
