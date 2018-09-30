package com.jkojote.library.domain.model.work;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.events.WorkFinishedEvent;
import com.jkojote.library.domain.shared.DomainEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Work extends DomainEntity {

    private String title;

    private LocalDate dateBegun;

    private LocalDate dateFinished;

    private boolean finished;

    private List<Author> authors;

    private Work(long id, String title,
                 Author author,
                 LocalDate dateBegun,
                 LocalDate dateFinished) {
        super(id);
        this.title = title;
        this.dateBegun = dateBegun;
        if (dateFinished != null) {
            this.finished = true;
        }
        this.dateFinished = dateFinished;
        this.authors = new LinkedList<>();
        this.authors.add(author);
    }

    private Work(long id, String title,
                 List<Author> authors,
                 LocalDate dateBegun,
                 LocalDate dateFinished) {
        super(id);
        this.title = title;
        this.dateBegun = dateBegun;
        if (dateFinished != null) {
            this.finished = true;
        }
        this.dateFinished = dateFinished;
        this.authors = new ArrayList<>(authors);
    }

    public static Work createNew(int id, String title, Author author) {
        checkNotNull(title);
        checkNotNull(author);
        Work work = new Work(id, title, author, LocalDate.now(), null);
        author.addWork(work);
        return work;
    }

    public static Work restore(int id, String title, Author author,
                               LocalDate dateBegun,
                               LocalDate dateFinished) {
        checkNotNull(title);
        checkNotNull(author);
        checkNotNull(dateBegun);
        if (dateFinished != null && dateBegun.compareTo(dateFinished) > 0)
            throw new IllegalStateException("work cannot be started after it is finished!");
        return new Work(id, title, author, dateBegun, dateFinished);
    }

    public static Work createNew(int id, String title, List<Author> authors) {
        checkNotNull(title);
        checkNotNull(authors);
        Work work = new Work(id, title, authors, LocalDate.now(), null);
        for (var author : authors)
            author.addWork(work);
        return work;
    }

    public static Work restore(int id, String title, List<Author> authors,
                               LocalDate dateBegun,
                               LocalDate dateFinished) {
        checkNotNull(authors);
        checkNotNull(title);
        checkNotNull(dateBegun);
        if (dateFinished != null && dateBegun.compareTo(dateFinished) > 0)
            throw new IllegalStateException("work cannot be started after it is finished!");
        return new Work(id, title, authors, dateBegun, dateFinished);
    }

    /**
     * Mark the work as finished
     * @return {@code true} if code has not been finished yet;
     *         {@code false} if you try to invoke it on finished work
     */
    public boolean finish() {
        if (!finished) {
            finished = true;
            dateFinished = LocalDate.now();
            notifyAllListeners(new WorkFinishedEvent(this, ""));
            return true;
        }
        return false;
    }

    public boolean isFinished() {
        return finished;
    }

    public List<Author> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public boolean removeAuthor(Author author) {
        if (!authors.contains(author))
            return false;
        authors.remove(author);
        if (author.filterWorks().contains(this)) {
            return author.removeWork(this);
        }
        return false;
    }

    public boolean addAuthor(Author author) {
        if (authors.contains(author))
            return false;
        authors.add(author);
        if (!author.filterWorks().contains(this)) {
            return author.addWork(this);
        }
        return false;
    }

    public LocalDate getDateBegun() {
        return dateBegun;
    }

    public LocalDate getDateFinished() {
        return dateFinished;
    }

    public String getTitle() {
        return title;
    }

}
