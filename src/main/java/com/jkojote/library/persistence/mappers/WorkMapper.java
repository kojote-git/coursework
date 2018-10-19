package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.LazyObjectFetcher;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import com.jkojote.library.persistence.lazy.LazyText;
import com.jkojote.library.values.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("workMapper")
class WorkMapper implements RowMapper<Work> {

    private ListFetcher<Work, Author> lazyAuthorListFetcher;

    private ListFetcher<Work, Subject> lazySubjectListFetcher;

    private LazyObjectFetcher<Work, Text> descriptionFetcher;

    @Autowired
    @Qualifier("authorsFetcher")
    public void setLazyAuthorListFetcher(ListFetcher<Work, Author> lazyAuthorListFetcher) {
        this.lazyAuthorListFetcher = lazyAuthorListFetcher;
    }

    @Autowired
    @Qualifier("descriptionFetcher")
    public void setDescriptionFetcher(LazyObjectFetcher<Work, Text> descriptionFetcher) {
        this.descriptionFetcher = descriptionFetcher;
    }

    @Autowired
    @Qualifier("subjectsFetcher")
    public void setLazySubjectListFetcher(ListFetcher<Work, Subject> lazySubjectListFetcher) {
        this.lazySubjectListFetcher = lazySubjectListFetcher;
    }

    @Override
    public Work mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id       = rs.getLong("id");
        String title    = rs.getString("title");
        LazyListImpl<Work, Author> authors  = new LazyListImpl<>(lazyAuthorListFetcher);
        LazyListImpl<Work, Subject> subjects = new LazyListImpl<>(lazySubjectListFetcher);
        Work work = Work.restore(id, title, authors, subjects);
        work.setDescription(new LazyText<>(work, descriptionFetcher));
        subjects.setParentEntity(work);
        authors.setParentEntity(work);
        subjects.seal();
        authors.seal();
        return work;
    }
}