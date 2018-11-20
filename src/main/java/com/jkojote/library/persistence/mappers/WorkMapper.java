package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.persistence.LazyObjectFetcher;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import com.jkojote.library.persistence.lazy.LazyText;
import com.jkojote.library.values.Text;
import com.neovisionaries.i18n.LanguageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


import static com.jkojote.library.domain.model.work.Work.WorkBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component("workMapper")
class WorkMapper implements RowMapper<Work> {

    private ListFetcher<Work, Author> lazyAuthorListFetcher;

    private ListFetcher<Work, Subject> lazySubjectListFetcher;

    private LazyObjectFetcher<Work, Text> descriptionFetcher;

    private DomainEventListener<Work> workStateListener;

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

    @Autowired
    @Qualifier("workStateListener")
    public void setWorkStateListener(DomainEventListener<Work> workStateListener) {
        this.workStateListener = workStateListener;
    }

    @Override
    public Work mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String
            title = rs.getString("title"),
            lang = rs.getString("lang");
        LazyListImpl<Work, Author> authors  = new LazyListImpl<>(lazyAuthorListFetcher);
        LazyListImpl<Work, Subject> subjects = new LazyListImpl<>(lazySubjectListFetcher);
        LanguageCode language = lang.equals("") ? LanguageCode.undefined :
                                                  LanguageCode.getByCode(lang);
        Work work = WorkBuilder.aWork()
                .withId(id)
                .withTitle(title)
                .withAuthors(authors)
                .withSubjects(subjects)
                .withLanguage(language)
                .build();
        work.setDescription(new LazyText<>(work, descriptionFetcher));
        subjects.setParentEntity(work);
        authors.setParentEntity(work);
        work.addEventListener(workStateListener);
        subjects.seal();
        authors.seal();
        return work;
    }
}