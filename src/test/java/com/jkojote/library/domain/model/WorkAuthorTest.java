package com.jkojote.library.domain.model;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.values.Name;
import com.jkojote.library.domain.model.work.Work;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkAuthorTest {

    private Author a1;

    private Author a2;

    private Work.WorkBuilder work;

    public WorkAuthorTest() {
        work = Work.WorkBuilder.aWork(true);
    }

    @Before
    public void init() {
        a1 = Author.createNew(1, Name.of("Davis", "Smith"));
        a2 = Author.createNew(2, Name.of("Rudie", "Gone"));
    }

    @Test
    public void addWork_AddsWorkToAuthorsWork() {
        Work w1 = work.withId(1).withTitle("Work1")
                .addAuthor(a1)
                .build();
        a1.addWork(w1);
        assertTrue(w1.getAuthors().contains(a1));
        assertTrue(a1.getWorks().contains(w1));
        // work already has this author
        assertFalse(w1.addAuthor(a1));
        // the a2 already has this work
        assertFalse(a1.addWork(w1));
    }

    @Test
    public void addAuthor_AddsAuthorToWorksAuthors() {
        Work w1 = work.withId(1).withTitle("Work1").addAuthor(a1).build();
        w1.addAuthor(a2);
        // author's been successfully added
        assertTrue(w1.getAuthors().contains(a2));
        assertTrue(a2.getWorks().contains(w1));
        // work already contains this author so it can't be added again
        assertFalse(w1.addAuthor(a2));
        // author already contains this work so it can't be added again
        assertFalse(a2.addWork(w1));
    }

    @Test
    public void removeAuthor_RemovesAuthorFromWorksAuthors() {
        Work w1 = work.withId(1).withTitle("Work1").addAuthor(a1).build();
        a1.addWork(w1);
        w1.addAuthor(a2);
        assertTrue(w1.removeAuthor(a2));
        assertFalse(w1.getAuthors().contains(a2));
        assertFalse(a2.getWorks().contains(w1));
        // assert that only one author's been removed
        // and no other author's been affected
        assertTrue(a1.getWorks().contains(w1));
        assertTrue(w1.getAuthors().contains(a1));
        // author's been successfully removed
        assertFalse(w1.removeAuthor(a2));
        assertFalse(a2.removeWork(w1));
    }

    @Test
    public void removeWork_RemovesWorkFromAuthorsWorks() {
        Work w1 = work.withId(1).withTitle("Work1").addAuthor(a1).build();
        Work w2 = work.withId(2).withTitle("Work2").addAuthor(a1).build();
        a1.addWork(w1);
        a1.addWork(w2);
        w1.addAuthor(a2);
        a2.addWork(w2);
        // work's been successfully removed
        assertTrue(a2.removeWork(w1));
        // a2 doesn't posses the work
        assertFalse(a2.getWorks().contains(w1));
        // work doesn't has such author as a2
        assertFalse(w1.getAuthors().contains(a2));
        // work's been already removed
        assertFalse(a2.removeWork(w1));

        assertTrue(a1.getWorks().contains(w1));
        assertTrue(w1.getAuthors().contains(a1));
        assertTrue(a1.getWorks().contains(w2));
        assertTrue(w2.getAuthors().contains(a1));
    }

}
