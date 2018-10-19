package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.values.DateRange;
import com.jkojote.library.values.Name;
import com.jkojote.library.persistence.mappers.AuthorMapper;
import com.jkojote.library.persistence.mappers.WorkMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class MappersTest {

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private WorkMapper workMapper;

    private ResultSet forWorkMapper;

    private ResultSet forAuthorMapper;

    public MappersTest() {
        try {
            initResultSetForAuthorMapper();
            initResultSetForWorkMapper();
        } catch (SQLException e) { }
    }

    @Test
    public void testWorkMapper() throws SQLException {
        Work work = workMapper.mapRow(forWorkMapper, 1);
        assertEquals("The Tower", work.getTitle());
        assertEquals(1, work.getId());

        work = workMapper.mapRow(forWorkMapper, 2);
        assertNotNull(work);
        assertEquals(2, work.getId());
        assertEquals("Two Towers", work.getTitle());
    }

    @Test
    public void testAuthorMapper() throws SQLException {
        Author author = authorMapper.mapRow(forAuthorMapper, 1);
        assertNotNull(author);
        assertEquals(1, author.getId());
        assertEquals(Name.of("Jordan", "Smith"), author.getName());

        author = authorMapper.mapRow(forAuthorMapper, 2);
        assertNotNull(author);
        assertEquals(2, author.getId());
        assertEquals(Name.of("Matthew", "Lincoln", "Jonson"), author.getName());
    }

    private void initResultSetForWorkMapper() throws SQLException {
        forWorkMapper = mock(ResultSet.class);
        Calendar calendar = new GregorianCalendar();
        calendar.set(76, Calendar.APRIL, 28);
        long begins = calendar.toInstant().toEpochMilli();
        calendar.set(78, Calendar.AUGUST, 15);
        long ends = calendar.toInstant().toEpochMilli();
        when(forWorkMapper.getDate("appearedBegins"))
                .thenReturn(new Date(begins))
                .thenReturn(null);
        when(forWorkMapper.getDate("appearedEnds"))
                .thenReturn(new Date(ends))
                .thenReturn(null);
        when(forWorkMapper.getString("title"))
                .thenReturn("The Tower")
                .thenReturn("Two Towers");
        when(forWorkMapper.getLong("id"))
                .thenReturn(1L)
                .thenReturn(2L);
    }

    private void initResultSetForAuthorMapper() throws SQLException {
        forAuthorMapper = mock(ResultSet.class);
        when(forAuthorMapper.getString("firstName"))
            .thenReturn("Jordan")
            .thenReturn("Matthew");
        when(forAuthorMapper.getString("lastName"))
            .thenReturn("Smith")
            .thenReturn("Jonson");
        when(forAuthorMapper.getString("middleName"))
            .thenReturn("")
            .thenReturn("Lincoln");
        when(forAuthorMapper.getLong("id"))
            .thenReturn(1L)
            .thenReturn(2L);
    }
}
