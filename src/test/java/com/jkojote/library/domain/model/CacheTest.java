package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class CacheTest {

    @Autowired
    @Qualifier("authorRepository")
    private DomainRepository<Author> authorRepository;

    @Autowired
    @Qualifier("workRepository")
    private DomainRepository<Work> workRepository;

    @Test
    public void test1() {
        Author a1 = authorRepository.findById(55);
        Work w1 =  workRepository.findById(66);
        Work w2 = workRepository.findById(77);
        Work w3 = workRepository.findById(88);

        // assert that this author has these three works
        assertTrue(a1.getWorks().contains(w1));
        assertTrue(a1.getWorks().contains(w2));
        assertTrue(a1.getWorks().contains(w3));

        // remove one of them from db
        workRepository.remove(w1);

        // this author still has this work
        // because w1 and a1 has different instances of the same entity
        assertTrue(a1.getWorks().contains(w1));

        // but if cache is disabled or problem with cache coherence is solved
        // this a1 won't have w1
        a1 = authorRepository.findById(55);
        assertFalse(a1.getWorks().contains(w1));
    }
}
