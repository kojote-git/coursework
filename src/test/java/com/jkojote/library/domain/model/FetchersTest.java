package com.jkojote.library.domain.model;

import com.jkojote.library.config.TestConfig;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.values.DateRange;
import com.jkojote.library.domain.shared.values.Name;
import com.jkojote.library.persistence.fetchers.LazyAuthorListFetcher;
import com.jkojote.library.persistence.fetchers.LazySubjectListFetcher;
import com.jkojote.library.persistence.fetchers.LazyWorkListFetcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class FetchersTest {

    @Autowired
    private LazyAuthorListFetcher authorFetcher;

    @Autowired
    private LazyWorkListFetcher workFetcher;

    @Autowired
    private LazySubjectListFetcher subjectFetcher;

    @Test
    public void testWorksFetcher() {
        // only id is used
        Author dawkins = Author.createNew(1, Name.of("", "", ""));
        Author doyle = Author.createNew(3, Name.of("", "", ""));
        var titles = workFetcher.fetchFor(dawkins).stream()
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
        Work w1 = Work.create(1, "W1", a1, DateRange.unknown());
        Work w2 = Work.create(3, "W2", a1, DateRange.unknown());
        var authorsNames = authorFetcher.fetchFor(w1).stream()
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
        Work w1 = Work.create(1, "W1", a1, DateRange.unknown());
        var subjects = subjectFetcher.fetchFor(w1);
        assertEquals(3, subjects.size());
        assertTrue(subjects.contains(Subject.of("Science")));
        assertTrue(subjects.contains(Subject.of("Biology")));
        assertTrue(subjects.contains(Subject.of("Religion")));
    }

    private <T> boolean contains(List<T> objects, T object) {
        return objects.stream().anyMatch(o -> o.equals(object));
    }
}
