package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("publisherMapper")
@Transactional
public class PublisherMapper implements RowMapper<Publisher> {

    private ListFetcher<Publisher, Book> bookListFetcher;

    @Autowired
    @Qualifier("booksFetcher")
    public void setBookListFetcher(ListFetcher<Publisher, Book> bookListFetcher) {
        this.bookListFetcher = bookListFetcher;
    }

    @Override
    public Publisher mapRow(ResultSet rs, int rowNum) throws SQLException {
        String name = rs.getString("name");
        long id = rs.getLong("id");
        LazyListImpl<Publisher, Book>
                bookList = new LazyListImpl<>(bookListFetcher);
        Publisher publisher = new Publisher(id, name, bookList);
        bookList.setParentEntity(publisher);
        bookList.seal();
        return publisher;
    }
}
