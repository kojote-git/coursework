package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Transactional
public class PublisherMapper implements RowMapper<Publisher> {

    private ListFetcher<Publisher, Book> bookListFetcher;

    @Autowired
    public void setBookListFetcher(ListFetcher<Publisher, Book> bookListFetcher) {
        this.bookListFetcher = bookListFetcher;
    }

    @Override
    public Publisher mapRow(ResultSet rs, int rowNum) throws SQLException {
        var name = rs.getString("name");
        var id = rs.getLong("id");
        var bookList = new LazyListImpl<>(bookListFetcher);
        var publisher = new Publisher(id, name, bookList);
        bookList.setParentEntity(publisher);
        bookList.seal();
        return publisher;
    }
}
