package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.domain.shared.domain.DomainList;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.values.Name;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import com.jkojote.library.persistence.listeners.AuthorStateListener;
import com.jkojote.library.persistence.fetchers.LazyWorkListFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("authorMapper")
public class AuthorMapper implements RowMapper<Author> {

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
        Author author = Author.restore(id, name, works);
        works.setParentEntity(author);
        works.seal();
        author.addEventListener(listener);
        return author;
    }
}
