package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.persistence.LazyObjectFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Blob;
import java.sql.SQLException;

@Component("bookFileFetcher")
@Transactional
class LazyBookFileFetcher implements LazyObjectFetcher<BookInstance, byte[]> {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LazyBookFileFetcher(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public byte[] fetchFor(BookInstance bookInstance) {
        String QUERY = "SELECT file FROM BookInstance WHERE id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(QUERY, bookInstance.getId());
        if (!rowSet.next())
            return new byte[0];
        Blob blob = (Blob) rowSet.getObject(1);
        try {
            return blob.getBytes(1, (int)blob.length());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
