package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.ListFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class LazySubjectListFetcher implements ListFetcher<Work, Subject> {

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private static final String QUERY =
        "SELECT subject FROM Subject s " +
          "INNER JOIN WorkSubject ws " +
             "ON s.id = ws.subjectId "+
          "WHERE ws.workId = :workId";

    @Autowired
    public LazySubjectListFetcher(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public List<Subject> fetchFor(Work work) {
        var params = new MapSqlParameterSource("workId", work.getId());
        return namedJdbcTemplate.query(QUERY, params, (rs, rn) -> Subject.of(rs.getString("subject")));
    }
}
