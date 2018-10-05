package com.jkojote.library.persistence.entities.mappers;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.shared.values.Name;
import com.jkojote.library.persistence.LazyListImpl;
import com.jkojote.library.persistence.internals.fetchers.LazyWorkListFetcher;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AuthorMapper implements RowMapper<Author> {

    private LazyWorkListFetcher lazyWorkListFetcher;

    public AuthorMapper(LazyWorkListFetcher lazyWorkListFetcher) {
        this.lazyWorkListFetcher = lazyWorkListFetcher;
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
        return author;
    }
}
