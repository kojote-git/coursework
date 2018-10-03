package com.jkojote.library.domain.model;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.shared.Name;
import com.jkojote.library.domain.model.work.Work;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class WorkAuthorTest {

    private Author a1;

    private Author a2;

    @Before
    public void init() {
        a1 = Author.createNew(1, Name.of("Davis", "Smith"));
        a2 = Author.createNew(2, Name.of("Rudie", "Gone"));
    }

    @Test
    public void addWork_AddsWorkToAuthorsWork() {
        Work work = Work.create(1, "Work1", a1, LocalDate.now());
        assertTrue(work.getAuthors().contains(a1));
        assertTrue(a1.getWorks().contains(work));
        // work already has this author
        assertFalse(work.addAuthor(a1));
        // the a2 already has this work
        assertFalse(a1.addWork(work));
    }

    @Test
    public void addAuthor_AddsAuthorToWorksAuthors() {
        Work work = Work.create(1, "Work1", a1, LocalDate.now());
        work.addAuthor(a2);
        // author's been successfully added
        assertTrue(work.getAuthors().contains(a2));
        assertTrue(a2.getWorks().contains(work));
        // work already contains this author so it can't be added again
        assertFalse(work.addAuthor(a2));
        // author already contains this work so it can't be added again
        assertFalse(a2.addWork(work));
    }

    @Test
    public void removeAuthor_RemovesAuthorFromWorksAuthors() {
        Work work = Work.create(1, "Work1", a1, LocalDate.now());
        work.addAuthor(a2);
        assertTrue(work.removeAuthor(a2));
        assertFalse(work.getAuthors().contains(a2));
        assertFalse(a2.getWorks().contains(work));
        // assert that only one author's been removed
        // and no other author's been affected
        assertTrue(a1.getWorks().contains(work));
        assertTrue(work.getAuthors().contains(a1));
        // author's been successfully removed
        assertFalse(work.removeAuthor(a2));
        assertFalse(a2.removeWork(work));
    }

    @Test
    public void removeWork_RemovesWorkFromAuthorsWorks() {
        Work work  = Work.create(1, "Work1", a1, LocalDate.now());
        Work work1 = Work.create(2, "Work2", a1, LocalDate.now());
        work.addAuthor(a2);
        a2.addWork(work1);
        // work's been successfully removed
        assertTrue(a2.removeWork(work));
        // a2 doesn't posses the work
        assertFalse(a2.getWorks().contains(work));
        // work doesn't has such author as a2
        assertFalse(work.getAuthors().contains(a2));
        // work's been already removed
        assertFalse(a2.removeWork(work));

        assertTrue(a1.getWorks().contains(work));
        assertTrue(work.getAuthors().contains(a1));
        assertTrue(a1.getWorks().contains(work1));
        assertTrue(work1.getAuthors().contains(a1));
    }

}
