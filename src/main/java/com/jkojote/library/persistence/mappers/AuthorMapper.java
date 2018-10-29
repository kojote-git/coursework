package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.values.Name;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import static com.jkojote.library.domain.model.author.Author.AuthorBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("authorMapper")
class AuthorMapper implements RowMapper<Author> {

    private ListFetcher<Author, Work> lazyWorkListFetcher;

    private DomainEventListener<Author> listener;

    @Autowired
    @Qualifier("worksFetcher")
    public void setLazyWorkListFetcher(ListFetcher<Author, Work> lazyWorkListFetcher) {
        this.lazyWorkListFetcher = lazyWorkListFetcher;
    }

    @Autowired
    @Qualifier("authorStateListener")
    public void setListener(DomainEventListener<Author> listener) {
        this.listener = listener;
    }

    public ListFetcher<Author, Work> getLazyWorkListFetcher() {
        return lazyWorkListFetcher;
    }

    @Override
    public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String firstName = rs.getString("firstName");
        String middleName = rs.getString("middleName");
        String lastName = rs.getString("lastName");
        Name name = Name.of(firstName, middleName, lastName);
        LazyListImpl<Author, Work> works =
                new LazyListImpl<>(lazyWorkListFetcher);
        Author author = AuthorBuilder.anAuthor()
                .withId(id)
                .withName(name)
                .withWorks(works)
                .build();
        works.setParentEntity(author);
        works.seal();
        author.addEventListener(listener);
        return author;
    }
}
