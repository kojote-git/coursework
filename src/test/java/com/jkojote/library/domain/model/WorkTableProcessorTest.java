package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.TableProcessor;
import com.jkojote.library.values.OrdinaryText;
import com.neovisionaries.i18n.LanguageCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.sql.ResultSet;
import java.sql.SQLException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class WorkTableProcessorTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("workTable")
    private TableProcessor<Work> workTable;

    @Test
    public void insert() {
        Work work = mock(Work.class);
        when(work.getId()).thenReturn(55L);
        when(work.getTitle()).thenReturn("title");
        when(work.getDescription()).thenReturn(OrdinaryText.of("description"));
        when(work.getLanguage()).thenReturn(LanguageCode.en);
        workTable.insert(work);
        String descr = jdbcTemplate.queryForObject("SELECT description FROM Work WHERE id = 55",
                this::extractDescription);
        assertNotNull(descr);
        assertEquals("description", descr);
    }

    @Test
    public void update() {
        String queryDescription = "SELECT description FROM Work WHERE id = 505";
        Work work = mock(Work.class);
        when(work.getId()).thenReturn(505L);
        when(work.getTitle()).thenReturn("title");
        when(work.getDescription()).thenReturn(OrdinaryText.of("1"))
                .thenReturn(OrdinaryText.of("2"));
        when(work.getLanguage()).thenReturn(LanguageCode.undefined);
        workTable.insert(work);
        String descr = jdbcTemplate.queryForObject(queryDescription, this::extractDescription);
        assertNotNull(descr);
        assertEquals("1", descr);
        workTable.update(work);
        descr = jdbcTemplate.queryForObject(queryDescription, this::extractDescription);
        assertNotNull(descr);
        assertEquals("2", descr);
    }

    private String extractDescription(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString(1);
    }

}
