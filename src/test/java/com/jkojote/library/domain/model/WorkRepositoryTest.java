package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.author.AuthorRepository;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.model.work.WorkRepository;
import com.jkojote.library.values.DateRange;
import com.jkojote.library.values.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
public class WorkRepositoryTest {

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void save_SavesWork() {
        Author a1 = Author.createNew(authorRepository.nextId(), Name.of("Samuel", "Saurus"));
        Work w1 = Work.create(workRepository.nextId(), "Saurus", a1, DateRange.unknown());
        assertTrue(workRepository.save(w1));
        assertTrue(workRepository.exists(w1));
        assertFalse(workRepository.save(w1));
        assertTrue(authorRepository.exists(a1));
    }
}
