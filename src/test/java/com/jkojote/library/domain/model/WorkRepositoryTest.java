package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.values.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jkojote.library.domain.model.author.Author.AuthorBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.jkojote.library.domain.model.work.Work.WorkBuilder;

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
        Author a1 = AuthorBuilder.anAuthor()
                .withId(authorRepository.nextId())
                .withName(Name.of("Samuel", "Saurus"))
                .build();
        Work w1 = WorkBuilder.aWork()
                .withId(workRepository.nextId())
                .withTitle("Saurus")
                .addAuthor(a1)
                .build();
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
        Author a1 = AuthorBuilder.anAuthor()
                .withId(authorRepository.nextId())
                .withName(Name.of("Peter", "Smith"))
                .build();
        Work w1 = WorkBuilder.aWork()
                .withId(workRepository.nextId())
                .withTitle("Smith")
                .addAuthor(a1)
                .build();
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
        String QUERY = "SELECT COUNT(*) FROM " + table + " WHERE "+ idColumn +" = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(QUERY, id);
        rs.next();
        return rs.getLong(1) == 1;
    }

    private long recordExistsBridge(long workId, long authorId) {
        String QUERY = "SELECT COUNT(*) FROM WorkAuthor WHERE workId = ? AND authorId = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(QUERY, workId, authorId);
        rs.next();
        return rs.getLong(1);
    }
}
