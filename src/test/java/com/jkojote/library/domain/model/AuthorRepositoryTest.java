package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.values.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class AuthorRepositoryTest {

    @Autowired
    private DomainRepository<Author> authorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void save_SavesAuthor() {
        long id = authorRepository.nextId();
        Author a1 = Author.createNew(id, Name.of("Jordan", "Smith"));
        assertTrue(authorRepository.save(a1));
        assertTrue(authorRepository.exists(a1));
        assertFalse(authorRepository.save(a1));
        assertEquals(1, authorRepository.findAll(a -> a.getId() == id).size());
    }

    @Test
    public void remove_RemovesAuthor() {
        long id = authorRepository.nextId();
        Author a2 = Author.createNew(id, Name.of("Peter", "Smith"));
        authorRepository.save(a2);
        authorRepository.remove(a2);

        assertFalse(authorRepository.exists(a2));
        assertFalse(recordExists("Author", id));
        assertFalse(recordExists("WorkAuthor", "authorId", id));
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
}
