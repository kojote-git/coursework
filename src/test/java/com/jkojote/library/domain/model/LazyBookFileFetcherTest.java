package com.jkojote.library.domain.model;

import com.jkojote.library.persistence.fetchers.ForBookInstanceConfig;
import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookFormat;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.book.instance.isbn.Isbn13;
import com.jkojote.library.files.FileInstance;
import com.jkojote.library.files.StandardFileInstance;
import com.jkojote.library.persistence.LazyObjectFetcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForBookInstanceConfig.class)
@DirtiesContext
public class LazyBookFileFetcherTest {

    @Autowired
    private LazyObjectFetcher<BookInstance, byte[]> fetcher;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Test
    public void test() {
        BookInstance bi = mock(BookInstance.class);
        Book b = mock(Book.class);
        when(b.getId()).thenReturn(1L);
        when(bi.getBook()).thenReturn(b);
        when(bi.getIsbn13()).thenReturn(Isbn13.of("978-0-1523-1221-1"));
        when(bi.getFormat()).thenReturn(BookFormat.PDF);
        initData(bi);
        byte[] array = fetcher.fetchFor(bi);
        assertNotNull(array);
    }

    private void initData(BookInstance bookInstance) {
        String INSERT =
                "INSERT INTO BookInstance (id, file, format, bookId, isbn13) " +
                        "VALUES (:id, :file, :format, :bookId, :isbn13)";
        FileInstance file = new StandardFileInstance("src/main/resources/file1.txt");
        Blob blob;
        try {
            blob = new SerialBlob(file.asBytes());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SqlParameterSource params = new MapSqlParameterSource("id", bookInstance.getId())
                .addValue("file", blob)
                .addValue("format", bookInstance.getFormat().asString())
                .addValue("bookId", bookInstance.getBook().getId())
                .addValue("isbn13", bookInstance.getIsbn13().toString());
        namedJdbcTemplate.update(INSERT, params);
    }

}

