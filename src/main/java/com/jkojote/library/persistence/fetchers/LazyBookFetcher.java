package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.persistence.ListFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("booksFetcher")
@Transactional
class LazyBookFetcher implements ListFetcher<Publisher, Book> {

    private JdbcTemplate jdbcTemplate;

    private RowMapper<Book> bookMapper;

    @Autowired
    public LazyBookFetcher(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    @Qualifier("bookMapper")
    public void setBookMapper(RowMapper<Book> bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Override
    public List<Book> fetchFor(Publisher entity) {
        String SELECT = "SELECT * FROM Book WHERE publisherId = ?";
        return jdbcTemplate.query(SELECT, bookMapper, entity.getId());
    }
}
