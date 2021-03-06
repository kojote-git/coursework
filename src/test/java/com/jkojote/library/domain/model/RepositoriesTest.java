package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.BridgeTableProcessor;
import com.jkojote.library.values.Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class RepositoriesTest {

    @Autowired
    private DomainRepository<Work> workRepository;

    @Autowired
    private DomainRepository<Author> authorRepository;


    @Autowired
    private BridgeTableProcessor<Work, Author> waBridgeTable;

    @Test
    public void test1_TryingSaveNewEntitiesAndChecksWhetherTheirStateIsManagedAfterPersistence() {
        Work.WorkBuilder work = Work.WorkBuilder.aWork(true);

        Author a1 = Author.createNew(authorRepository.nextId(), Name.of("Oliver", "Peterson"));
        Author a2 = Author.createNew(authorRepository.nextId(), Name.of("Jordan", "Smith"));
        Author a3 = Author.createNew(authorRepository.nextId(), Name.of("Peter", "Lincoln"));

        Work w1 = work.withId(workRepository.nextId())
                .withTitle("Spin").addAuthor(a1)
                .build();
        Work w2 = work.withId(workRepository.nextId())
                .withTitle("Soul").addAuthor(a1)
                .build();
        Work w3 = work.withId(workRepository.nextId())
                .withTitle("Newton").addAuthor(a2)
                .build();
        Work w4 = work.withId(workRepository.nextId())
                .withTitle("Tesla").addAuthor(a2)
                .build();
        a1.addWork(w1);
        a1.addWork(w2);
        a2.addWork(w3);

        // author's been saved
        assertTrue(authorRepository.save(a1));
        assertTrue(workRepository.exists(w1));
        assertTrue(workRepository.exists(w2));

        w2.addAuthor(a3);
        // author's been saved as well
        assertTrue(authorRepository.exists(a3));

        a3.addWork(w3);
        a3.addWork(w4);
        a2.addWork(w4);
        // these works have been saved
        assertTrue(workRepository.exists(w3));
        assertTrue(workRepository.exists(w4));

        // bridge table has relationships between a3 - w3, a3 - w4 and a2 - w4
        assertTrue(waBridgeTable.exists(w3, a3));
        assertTrue(waBridgeTable.exists(w4, a3));
        assertTrue(waBridgeTable.exists(w4, a2));

        assertTrue(workRepository.remove(w4));

        // records have been deleted from bridge table
        assertFalse(waBridgeTable.exists(w4, a3));
        assertFalse(waBridgeTable.exists(w4, a2));

        // database doesn't have such relationships between a3 - w4 and a2 - w4
        // so a3 and a3 don't have either
        assertFalse(a3.getWorks().contains(w4));
        assertFalse(a2.getWorks().contains(w4));
    }

    @Test
    public void test2_FetchesEntitiesFromDatabaseAndChecksWhetherTheyAreManaged() {
        Author a1 = authorRepository.findById(1);
        Work w1 = workRepository.findById(3);
        assertNotNull(a1);
        assertNotNull(w1);
        assertEquals(Name.of("Richard", "Dawkins"), a1.getName());
        assertEquals("The Hound of the Baskervilles", w1.getTitle());

        // after adding work to authors work
        // state listener writes corresponding
        // record into bridge table WorkAuthor
        a1.addWork(w1);
        assertTrue(waBridgeTable.exists(w1, a1));
        a1.removeWork(w1);
        assertFalse(waBridgeTable.exists(w1, a1));
    }

}
