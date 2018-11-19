package com.jkojote.library.domain.model;

import com.jkojote.library.persistence.tables.ConfigForBookInstanceTableProcessor;
import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookFormat;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.book.instance.isbn.Isbn13;
import com.jkojote.library.files.FileInstance;
import com.jkojote.library.files.StandardFileInstance;
import com.jkojote.library.persistence.TableProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConfigForBookInstanceTableProcessor.class)
@DirtiesContext
public class BookInstanceTableProcessorTest {

    @Autowired
    private TableProcessor<BookInstance> bookInstanceTable;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void insert() throws SQLException  {
        Book book = mock(Book.class);
        Isbn13 isbn13 = Isbn13.of("978-0152312211");
        FileInstance f = new StandardFileInstance("src/main/resources/file1.txt");
        BookInstance bi = new BookInstance(55, book, isbn13, BookFormat.PDF);
        bi.setFile(f);
        bookInstanceTable.insert(bi);
        byte[] bytes = jdbcTemplate.queryForObject("SELECT file FROM BookInstance", (rs, rn) -> {
            return rs.getBytes(1);
        });
        assertNotNull(bytes);
        assertArrayEquals(f.asBytes(), bytes);
    }

    @Test
    public void update() {
        Book book = mock(Book.class);
        Isbn13 isbn13 = Isbn13.of("978-0152312211");
        FileInstance f = new StandardFileInstance("src/main/resources/file1.txt");
        FileInstance f1 = new StandardFileInstance("src/main/resources/file2.pdf");
        FileInstance cover = new StandardFileInstance("src/main/resources/file2.pdf");
        when(book.getId()).thenReturn(1L);
        BookInstance bi = new BookInstance(55, book, isbn13, BookFormat.PDF);
        bi.setFile(f);
        bookInstanceTable.insert(bi);
        bi.setFile(f1);
        bi.setCover(cover);
        bookInstanceTable.update(bi);
        byte[] bytes = jdbcTemplate.queryForObject("SELECT file FROM BookInstance", (rs, rn) -> {
            return rs.getBytes(1);
        });
        byte[] coverBytes = jdbcTemplate.queryForObject("SELECT cover FROM BookInstance", (rs, rn) -> {
            return rs.getBytes(1);
        });
        assertNotNull(bytes);
        assertArrayEquals(f1.asBytes(), bytes);
        assertArrayEquals(cover.asBytes(), coverBytes);
    }
}
