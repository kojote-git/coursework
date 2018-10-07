package com.jkojote.library.domain.model;

import com.jkojote.library.config.TestConfig;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.author.AuthorRepository;
import com.jkojote.library.domain.shared.values.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void save_SavesAuthor() {
        long id = authorRepository.nextId();
        Author a1 = Author.createNew(id, Name.of("Jordan", "Smith"));
        assertTrue(authorRepository.save(a1));
        assertTrue(authorRepository.exists(a1));
        assertFalse(authorRepository.save(a1));
        assertEquals(1, authorRepository.findAll(a -> a.getId() == id).size());
    }
}
