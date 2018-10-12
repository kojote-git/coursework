package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.values.DateRange;
import com.jkojote.library.values.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class WorkRepositoryTest {

    @Autowired
    private DomainRepository<Work> workRepository;

    @Autowired
    private DomainRepository<Author> authorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void save_SavesWork() {
        Author a1 = Author.createNew(authorRepository.nextId(), Name.of("Samuel", "Saurus"));
        Work w1 = Work.create(workRepository.nextId(), "Saurus", a1, DateRange.unknown());
        assertTrue(workRepository.save(w1));
        assertTrue(workRepository.exists(w1));
        assertFalse(workRepository.save(w1));
        assertTrue(authorRepository.exists(a1));

        assertTrue(recordExists("Work", w1.getId()));
        assertTrue(recordExists("Author", a1.getId()));
        assertEquals(1, recordExistsBridge(w1.getId(), a1.getId()));
    }

    @Test
    public void remove_RemovesWorkAndChecksIfRecordsWhereDeletedFromRelatedToWorkTables() {
        Author a1 = Author.createNew(authorRepository.nextId(), Name.of("Peter", "Smith"));
        Work w1 = Work.create(workRepository.nextId(), "Smith", a1, DateRange.unknown());
        workRepository.save(w1);
        workRepository.remove(w1);

        assertFalse(workRepository.exists(w1));
        assertFalse(recordExists("Work", w1.getId()));
        assertEquals(0, recordExistsBridge(w1.getId(), a1.getId()));
    }

    private boolean recordExists(String table, long id) {
        return recordExists(table, "id", id);
    }

    private boolean recordExists(String table, String idColumn, long id) {
        var QUERY = "SELECT COUNT(*) FROM " + table + " WHERE "+ idColumn +" = ?";
        var rs = jdbcTemplate.queryForRowSet(QUERY, id);
        rs.next();
        return rs.getLong(1) == 1;
    }

    private long recordExistsBridge(long workId, long authorId) {
        var QUERY = "SELECT COUNT(*) FROM WorkAuthor WHERE workId = ? AND authorId = ?";
        var rs = jdbcTemplate.queryForRowSet(QUERY, workId, authorId);
        rs.next();
        return rs.getLong(1);
    }
}
