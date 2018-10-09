package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.mappers.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class LazyBookFetcher implements ListFetcher<Publisher, Book> {

    private JdbcTemplate jdbcTemplate;

    private BookMapper bookMapper;

    @Autowired
    public LazyBookFetcher(JdbcTemplate jdbcTemplate,
                           BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }

    @Override
    public List<Book> fetchFor(Publisher entity) {
        var SELECT = "SELECT * FROM Book WHERE publisherId = ?";
        return jdbcTemplate.query(SELECT, bookMapper, entity.getId());
    }
}