package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.LazyObject;
import com.jkojote.library.persistence.LazyObjectFetcher;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.values.Name;
import com.jkojote.library.values.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class FetchersTest {

    @Autowired
    private ListFetcher<Work, Author> authorFetcher;

    @Autowired
    private ListFetcher<Author, Work> workFetcher;

    @Autowired
    private ListFetcher<Work, Subject> subjectFetcher;

    @Autowired
    private DomainRepository<Work> workRepository;

    @Test
    public void testWorksFetcher() {
        // only id is used
        Author dawkins = Author.createNew(1, Name.of("", "", ""));
        Author doyle = Author.createNew(3, Name.of("", "", ""));
        List<String> titles = workFetcher.fetchFor(dawkins).stream()
                .map(Work::getTitle)
                .collect(Collectors.toList());
        assertEquals(2, titles.size());
        assertTrue(contains(titles, "The God Delusion"));
        assertTrue(contains(titles, "The Selfish Gene"));
        titles = workFetcher.fetchFor(doyle).stream()
                .map(Work::getTitle)
                .collect(Collectors.toList());
        assertEquals(2, titles.size());
        assertTrue(titles.contains("The Hound of the Baskervilles"));
        assertTrue(titles.contains("A Study in Scarlet"));
    }

    @Test
    public void testAuthorFetcher() {
        Author a1 = Author.createNew(1, Name.of("", "", ""));
        // only id is used to fetch authors
        Work w1 = Work.create(1, "W1", a1);
        Work w2 = Work.create(3, "W2", a1);
        List<Name> authorsNames = authorFetcher.fetchFor(w1).stream()
                .map(Author::getName)
                .collect(Collectors.toList());
        assertEquals(1, authorsNames.size());
        assertTrue(authorsNames.contains(Name.of("Richard", "Dawkins")));

        authorsNames = authorFetcher.fetchFor(w2).stream()
                .map(Author::getName)
                .collect(Collectors.toList());
        assertEquals(1, authorsNames.size());
        assertTrue(authorsNames.contains(Name.of("Arthur", "Conan", "Doyle")));
    }

    @Test
    public void testSubjectFetcher() {
        Author a1 = Author.createNew(1, Name.of("", "", ""));
        // only id is used to fetch authors
        Work w1 = Work.create(1, "W1", a1);
        List<Subject> subjects = subjectFetcher.fetchFor(w1);
        assertEquals(3, subjects.size());
        assertTrue(subjects.contains(Subject.of("Science")));
        assertTrue(subjects.contains(Subject.of("Biology")));
        assertTrue(subjects.contains(Subject.of("Religion")));
    }

    @Test
    public void testTextIsFetchedLazily() {
        Work work = workRepository.findById(5);
        Text descr = work.getDescription();
        assertTrue(descr instanceof LazyObject);
        LazyObject lazy = (LazyObject) descr;
        assertFalse(lazy.isFetched());
        // fetch description from database
        descr.toString();
        assertTrue(lazy.isFetched());
    }

    private <T> boolean contains(List<T> objects, T object) {
        return objects.stream().anyMatch(o -> o.equals(object));
    }
}
