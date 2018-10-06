package com.jkojote.library.domain.model;

import com.jkojote.library.config.TestConfig;
import com.jkojote.library.domain.shared.values.DateRange;
import com.jkojote.library.domain.shared.values.Name;
import com.jkojote.library.persistence.mappers.AuthorMapper;
import com.jkojote.library.persistence.mappers.WorkMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@ContextConfiguration(classes = TestConfig.class)
public class MappersTest {

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private WorkMapper workMapper;

    private ResultSet forWorMapper;

    private ResultSet forAuthorMapper;

    public MappersTest() {
        try {
            initResultSetForAuthorMapper();
            initResultSetForWorkMapper();
        } catch (SQLException e) { }
    }

    @Test
    public void testWorkMapper() throws SQLException {
        var work = workMapper.mapRow(forWorMapper, 1);
        assertEquals("The Tower", work.getTitle());
        assertEquals(1, work.getId());

        work = workMapper.mapRow(forWorMapper, 2);
        assertNotNull(work);
        assertEquals(2, work.getId());
        assertEquals("Two Towers", work.getTitle());
        assertEquals(work.whenAppeared(), DateRange.unknown());
    }

    @Test
    public void testAuthorMapper() throws SQLException {
        var author = authorMapper.mapRow(forAuthorMapper, 1);
        assertNotNull(author);
        assertEquals(1, author.getId());
        assertEquals(Name.of("Jordan", "Smith"), author.getName());

        author = authorMapper.mapRow(forAuthorMapper, 2);
        assertNotNull(author);
        assertEquals(2, author.getId());
        assertEquals(Name.of("Matthew", "Lincoln", "Jonson"), author.getName());
    }

    private void initResultSetForWorkMapper() throws SQLException {
        forWorMapper = mock(ResultSet.class);
        var calendar = new GregorianCalendar();
        calendar.set(76, Calendar.APRIL, 28);
        long begins = calendar.toInstant().toEpochMilli();
        calendar.set(78, Calendar.AUGUST, 15);
        long ends = calendar.toInstant().toEpochMilli();
        when(forWorMapper.getDate("appearedBegins")).thenReturn(new Date(begins))
                .thenReturn(null);
        when(forWorMapper.getDate("appearedEnds")).thenReturn(new Date(ends))
                .thenReturn(null);
        when(forWorMapper.getString("title")).thenReturn("The Tower")
                .thenReturn("Two Towers");
        when(forWorMapper.getLong("id")).thenReturn(1L);
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
