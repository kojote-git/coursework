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

@Component("bookInstanceCoverFetcher")
@Transactional
@SuppressWarnings("Duplicates")
public class LazyBookInstanceCoverFetcher implements LazyObjectFetcher<BookInstance, byte[]> {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LazyBookInstanceCoverFetcher(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public byte[] fetchFor(BookInstance bookInstance) {
        String query = "SELECT cover FROM BookInstance WHERE id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, bookInstance.getId());
        if (!rowSet.next())
            return new byte[0];
        Object res = rowSet.getObject(1);
        if (res instanceof Blob) {
            try {
                Blob b = (Blob) res;
                return b.getBytes(1, (int)b.length());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (res instanceof byte[]) {
            return (byte[])res;
        } else {
            return null;
        }
    }
}
