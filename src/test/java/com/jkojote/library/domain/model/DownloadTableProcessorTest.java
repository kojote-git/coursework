package com.jkojote.library.domain.model;

import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.reader.Download;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.persistence.TableProcessor;
import com.jkojote.library.persistence.tables.ConfigForDownloadTableProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConfigForDownloadTableProcessor.class)
@DirtiesContext
public class DownloadTableProcessorTest {

    @Autowired
    private TableProcessor<Download> downloadTable;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void exists() {
        Download d = mock(Download.class);
        Reader r = mock(Reader.class);
        BookInstance b = mock(BookInstance.class);
        when(r.getId()).thenReturn(1L);
        when(b.getId()).thenReturn(1L);
        when(d.getReader()).thenReturn(r);
        when(d.getInstance()).thenReturn(b);
        assertTrue(downloadTable.exists(d));
    }

    @Test
    public void insert() {
        Reader r = mock(Reader.class);
        BookInstance b = mock(BookInstance.class);
        when(r.getId()).thenReturn(2L);
        when(b.getId()).thenReturn(2L);
        Download d = new Download(r, b, LocalDateTime.now(), 10);
        assertFalse(downloadTable.exists(d));
        assertTrue(downloadTable.insert(d));
        assertTrue(downloadTable.exists(d));
        String selectCount =
            "SELECT COUNT(*) FROM Download WHERE readerId = ? AND bookInstanceId = ?";
        Long count = jdbcTemplate.queryForObject(selectCount, (rs, rn) -> {
            return rs.getLong(1);
        }, 2, 2);
        assertNotNull(count);
        assertEquals(1, count.longValue());
    }

    @Test
    public void update() {
        Reader r = mock(Reader.class);
        BookInstance b = mock(BookInstance.class);
        when(r.getId()).thenReturn(1L);
        when(b.getId()).thenReturn(1L);
        Download d = new Download(r, b, LocalDateTime.now(), 5);
        assertTrue(downloadTable.update(d));
        String queryRating =
            "SELECT readerRating FROM Download WHERE readerId = ? AND bookInstanceId = ?";
        int rating = jdbcTemplate.queryForObject(queryRating, (rs, rn) -> {
            return rs.getInt(1);
        }, 1L, 1L);
        assertEquals(5, rating);
    }
}
