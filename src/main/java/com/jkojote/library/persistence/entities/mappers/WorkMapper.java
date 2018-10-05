package com.jkojote.library.persistence.entities.mappers;

import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.LazyListImpl;
import com.jkojote.library.persistence.internals.fetchers.LazyAuthorListFetcher;
import com.jkojote.library.persistence.internals.fetchers.LazySubjectListFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WorkMapper implements RowMapper<Work> {

    private LazyAuthorListFetcher lazyAuthorListFetcher;

    private LazySubjectListFetcher lazySubjectListFetcher;

    @Autowired
    public WorkMapper(LazyAuthorListFetcher lazyAuthorListFetcher,
                      LazySubjectListFetcher lazySubjectListFetcher) {
        this.lazyAuthorListFetcher = lazyAuthorListFetcher;
        this.lazySubjectListFetcher = lazySubjectListFetcher;
    }

    public LazyAuthorListFetcher getLazyAuthorListFetcher() {
        return lazyAuthorListFetcher;
    }

    public LazySubjectListFetcher getLazySubjectListFetcher() {
        return lazySubjectListFetcher;
    }

    @Override
    public Work mapRow(ResultSet rs, int rowNum) throws SQLException {
        var id       = rs.getLong("id");
        var title    = rs.getString("title");
        var written  = rs.getDate("written").toLocalDate();
        var authors  = new LazyListImpl<>(lazyAuthorListFetcher);
        var subjects = new LazyListImpl<>(lazySubjectListFetcher);
        var work     = Work.restore(id, title, written, authors, subjects);
        subjects.setParentEntity(work);
        authors.setParentEntity(work);
        subjects.seal();
        authors.seal();
        return work;
    }
}