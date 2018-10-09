package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.values.Name;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import com.jkojote.library.persistence.listeners.AuthorStateListener;
import com.jkojote.library.persistence.fetchers.LazyWorkListFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AuthorMapper implements RowMapper<Author> {

    private LazyWorkListFetcher lazyWorkListFetcher;

    private AuthorStateListener listener;

    @Autowired
    public void setLazyWorkListFetcher(LazyWorkListFetcher lazyWorkListFetcher) {
        this.lazyWorkListFetcher = lazyWorkListFetcher;
    }

    @Autowired
    public void setListener(AuthorStateListener listener) {
        this.listener = listener;
    }

    public LazyWorkListFetcher getLazyWorkListFetcher() {
        return lazyWorkListFetcher;
    }

    @Override
    public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
        var id = rs.getLong("id");
        var firstName = rs.getString("firstName");
        var middleName = rs.getString("middleName");
        var lastName = rs.getString("lastName");
        var name = Name.of(firstName, middleName, lastName);
        var works = new LazyListImpl<>(lazyWorkListFetcher);
        var author = Author.restore(id, name, works);
        works.setParentEntity(author);
        works.seal();
        author.addEventListener(listener);
        return author;
    }
}
