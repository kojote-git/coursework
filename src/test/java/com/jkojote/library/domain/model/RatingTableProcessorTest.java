package com.jkojote.library.domain.model;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.reader.Rating;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.persistence.TableProcessor;
import com.jkojote.library.persistence.tables.ConfigForRatingProcessorTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ConfigForRatingProcessorTable.class)
public class RatingTableProcessorTest {

    @Autowired
    @Qualifier("ratingTable")
    private TableProcessor<Rating> ratingTable;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void exists() {
        Reader reader = mock(Reader.class);
        Book book = mock(Book.class);
        when(reader.getId()).thenReturn(1L);
        when(book.getId()).thenReturn(1L);
        Rating rating = new Rating(reader, book, 6);
        assertTrue(ratingTable.exists(rating));
        when(book.getId()).thenReturn(2L);
        assertTrue(ratingTable.exists(rating));
        when(book.getId()).thenReturn(3L);
        assertFalse(ratingTable.exists(rating));
    }

    @Test
    @DirtiesContext
    public void insert() {
        Reader reader = mock(Reader.class);
        Book book = mock(Book.class);
        Rating rating = new Rating(reader, book, 2);
        when(reader.getId()).thenReturn(1L);
        when(book.getId()).thenReturn(4L);
        assertFalse(ratingTable.exists(rating));
        ratingTable.insert(rating);
        assertTrue(ratingTable.exists(rating));
    }

    @Test
    @DirtiesContext
    public void delete() {
        Reader reader = mock(Reader.class);
        Book book = mock(Book.class);
        Rating rating = new Rating(reader, book, 2);
        when(reader.getId()).thenReturn(1L);
        when(book.getId()).thenReturn(1L);
        assertTrue(ratingTable.exists(rating));
        ratingTable.delete(rating);
        assertFalse(ratingTable.exists(rating));
    }

    @Test
    @DirtiesContext
    public void update() {
        Reader reader = mock(Reader.class);
        Book book = mock(Book.class);
        Rating rating = new Rating(reader, book, 10);
        when(reader.getId()).thenReturn(1L);
        when(book.getId()).thenReturn(1L);
        ratingTable.update(rating);
        String select = "SELECT rating FROM Rating WHERE bookId = 1 AND readerId = 1";
        int ratingValue = jdbcTemplate.queryForObject(select, (rs, rn) -> {
            return rs.getInt(1);
        });
        assertEquals(10, ratingValue);
    }

}
