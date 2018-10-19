package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.Utils;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.values.DateRange;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import com.jkojote.library.persistence.fetchers.LazyAuthorListFetcher;
import com.jkojote.library.persistence.fetchers.LazySubjectListFetcher;
import com.jkojote.library.values.DateRangePrecision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component("workMapper")
public class WorkMapper implements RowMapper<Work> {

    private ListFetcher<Work, Author> lazyAuthorListFetcher;

    private ListFetcher<Work, Subject> lazySubjectListFetcher;


    @Autowired
    @Qualifier("authorsFetcher")
    public void setLazyAuthorListFetcher(ListFetcher<Work, Author> lazyAuthorListFetcher) {
        this.lazyAuthorListFetcher = lazyAuthorListFetcher;
    }

    @Autowired
    @Qualifier("subjectsFetcher")
    public void setLazySubjectListFetcher(ListFetcher<Work, Subject> lazySubjectListFetcher) {
        this.lazySubjectListFetcher = lazySubjectListFetcher;
    }

    public ListFetcher<Work, Author> getLazyAuthorListFetcher() {
        return lazyAuthorListFetcher;
    }

    public ListFetcher<Work, Subject> getLazySubjectListFetcher() {
        return lazySubjectListFetcher;
    }

    @Override
    public Work mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id       = rs.getLong("id");
        String title    = rs.getString("title");
        LazyListImpl<Work, Author> authors  = new LazyListImpl<>(lazyAuthorListFetcher);
        LazyListImpl<Work, Subject> subjects = new LazyListImpl<>(lazySubjectListFetcher);
        Work work = Work.restore(id, title, authors, subjects);
        subjects.setParentEntity(work);
        authors.setParentEntity(work);
        subjects.seal();
        authors.seal();
        return work;
    }
}