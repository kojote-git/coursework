package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.persistence.TableProcessor;
import com.jkojote.types.Email;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.jkojote.library.domain.model.reader.Reader.ReaderBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class ReaderTableProcessorTest {

    @Autowired
    private TableProcessor<Reader> readerTable;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void exists() {
        Reader r = mock(Reader.class);
        when(r.getId()).thenReturn(1L).thenReturn(2L);
        assertTrue(readerTable.exists(r));
        assertFalse(readerTable.exists(r));
    }

    @Test
    public void insert() {
        Reader r = ReaderBuilder.aReader()
                .withEmail(Email.of("email1@email.com"))
                .withId(33)
                .withPassword("password")
                .build();
        assertFalse(readerTable.exists(r));
        readerTable.insert(r);
        assertTrue(readerTable.exists(r));
    }

    @Test
    public void update() {
        String emailBefore = "email2@mail.com";
        String emailAfter = "emaol3@mail.com";
        Reader r = ReaderBuilder.aReader()
                .withEmail(Email.of(emailBefore))
                .withId(34)
                .withPassword("password")
                .build();
        assertTrue(readerTable.insert(r));
        r.setEmail(Email.of(emailAfter));
        assertTrue(readerTable.update(r));
        String queryEmail = "SELECT email FROM Reader WHERE id = ?";
        String actualEmail = jdbcTemplate.queryForObject(queryEmail, (rs, rn) -> {
            return rs.getString(1);
        }, r.getId());
        assertEquals(emailAfter, actualEmail);
    }

    @Test
    @DirtiesContext
    public void delete() {
        Reader r = mock(Reader.class);
        when(r.getId()).thenReturn(1L);
        assertTrue(readerTable.delete(r));
        assertFalse(readerTable.exists(r));
        String queryCount = "SELECT COUNT(*) FROM Reader WHERE id = ?";
        long count = jdbcTemplate.queryForObject(queryCount, (rs, rn) -> {
            return rs.getLong(1);
        }, r.getId());
        assertEquals(0, count);
    }
}
