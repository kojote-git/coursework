package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.book.BookRepository;
import com.jkojote.library.domain.model.book.instance.BookFormat;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.book.instance.isbn.Isbn13;
import com.jkojote.library.persistence.fetchers.LazyBookFileFetcher;
import com.jkojote.library.persistence.lazy.LazyFileInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BookInstanceMapper implements RowMapper<BookInstance> {

    private BookRepository bookRepository;

    private LazyBookFileFetcher fileFetcher;

    @Autowired
    public BookInstanceMapper(LazyBookFileFetcher fetcher) {
        this.fileFetcher = fetcher;
    }

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
        var id = rs.getLong("id");
        var format = BookFormat.of(rs.getString("format"));
        var isbn13 = Isbn13.of(rs.getString("isbn13"));
        var bookId = rs.getLong("bookId");
        var book = bookRepository.findById(bookId);
        var bookInstance = new BookInstance(id, book, isbn13, format);
        var file = new LazyFileInstance<>(bookInstance, fileFetcher);
        bookInstance.setFile(file);
        return bookInstance;
    }
}
