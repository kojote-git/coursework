package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.persistence.LazyObjectFetcher;
import com.jkojote.library.persistence.ListFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("bookInstancesFetcher")
@Transactional
public class LazyBookInstancesListFetcher implements ListFetcher<Book, BookInstance> {

    private JdbcTemplate jdbcTemplate;

    private RowMapper<BookInstance> bookInstanceMapper;

    @Autowired
    public LazyBookInstancesListFetcher(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    @Qualifier("bookInstanceMapper")
    public void setBookInstanceMapper(RowMapper<BookInstance> bookInstanceMapper) {
        this.bookInstanceMapper = bookInstanceMapper;
    }

    @Override
    public List<BookInstance> fetchFor(Book book) {
        String QUERY =
            "SELECT id, isbn13, bookId, format FROM BookInstance WHERE bookId = ?";
        return jdbcTemplate.query(QUERY, bookInstanceMapper, book.getId());
    }
}
