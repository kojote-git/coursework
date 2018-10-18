package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookFormat;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.book.instance.isbn.Isbn13;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.LazyObjectFetcher;
import com.jkojote.library.persistence.fetchers.LazyBookFileFetcher;
import com.jkojote.library.persistence.lazy.LazyFileInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("bookInstanceMapper")
public class BookInstanceMapper implements RowMapper<BookInstance> {

    private DomainRepository<Book> bookRepository;

    private LazyObjectFetcher<BookInstance, byte[]> fileFetcher;

    @Autowired
    public BookInstanceMapper(
            @Qualifier("bookFileFetcher")
            LazyObjectFetcher<BookInstance, byte[]> fetcher) {
        this.fileFetcher = fetcher;
    }

    @Autowired
    public void setBookRepository(DomainRepository<Book> bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        BookFormat format = BookFormat.of(rs.getString("format"));
        Isbn13 isbn13 = Isbn13.of(rs.getString("isbn13"));
        long bookId = rs.getLong("bookId");
        Book book = bookRepository.findById(bookId);
        BookInstance bookInstance = new BookInstance(id, book, isbn13, format);
        LazyFileInstance<BookInstance> file = new LazyFileInstance<>(bookInstance, fileFetcher);
        bookInstance.setFile(file);
        return bookInstance;
    }
}
