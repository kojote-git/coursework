package com.jkojote.library.domain.model.work;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.events.SubjectAddedEvent;
import com.jkojote.library.domain.model.work.events.SubjectRemovedEvent;
import com.jkojote.library.domain.shared.DomainEntity;
import com.jkojote.library.domain.shared.DomainArrayList;
import com.jkojote.library.domain.shared.Utils;
import com.jkojote.library.persistence.DomainList;

import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkNotNull;

public class Work extends DomainEntity {

    private String title;

    private LocalDate written;

    private DomainList<Author> authors;

    private DomainList<Subject> subjects;

    private Work(long id,
                 String title,
                 LocalDate written,
                 Author author) {
        super(id);
        this.title    = title;
        this.authors  = new DomainArrayList<>();
        this.subjects = new DomainArrayList<>();
        this.written  = written;
        this.authors.add(author);
    }

    private Work(long id, String title,
                 LocalDate written,
                 DomainList<Author> authors,
                 DomainList<Subject> subjects) {
        super(id);
        this.title    = title;
        this.authors  = authors;
        this.written  = written;
        this.subjects = subjects;
    }

    public static Work create(long id, String title, Author author, LocalDate written) {
        checkNotNull(title);
        checkNotNull(author);
        Work work = new Work(id, title, written, author);
        author.addWork(work);
        return work;
    }

    public static Work create(long id, String title, DomainList<Author> authors, LocalDate written) {
        checkNotNull(title);
        checkNotNull(authors);
        Work work = new Work(id, title, written, authors, new DomainArrayList<>());
        for (var author : authors)
            author.addWork(work);
        return work;
    }

    public static Work restore(int id, String title,
                               LocalDate written,
                               DomainList<Author> authors,
                               DomainList<Subject> subjects) {
        checkNotNull(authors);
        checkNotNull(title);
        checkNotNull(subjects);
        return new Work(id, title, written, authors, subjects);
    }

    public DomainList<Author> getAuthors() {
        return Utils.unmodifiableDomainList(authors);
    }

    public DomainList<Subject> getSubjects() {
        return Utils.unmodifiableDomainList(subjects);
    }

    public boolean addSubject(Subject subject) {
        if (subjects.contains(subject))
            return false;
        subjects.add(subject);
        notifyAllListeners(new SubjectAddedEvent(this, subject, null));
        return true;
    }

    public boolean removeSubject(Subject subject) {
        if (!subjects.contains(subject))
            return false;
        subjects.remove(subject);
        notifyAllListeners(new SubjectRemovedEvent(this, subject, null));
        return true;
    }

    public boolean removeAuthor(Author author) {
        if (!authors.contains(author))
            return false;
        authors.remove(author);
        if (author.getWorks().contains(this)) {
            return author.removeWork(this);
        }
        return false;
    }

    public boolean addAuthor(Author author) {
        if (authors.contains(author))
            return false;
        authors.add(author);
        if (!author.getWorks().contains(this)) {
            return author.addWork(this);
        }
        return false;
    }

    public LocalDate whenWritten() {
        return written;
    }

    public String getTitle() {
        return title;
    }

}
