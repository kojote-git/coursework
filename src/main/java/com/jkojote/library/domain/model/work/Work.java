package com.jkojote.library.domain.model.work;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.events.AuthorAddedEvent;
import com.jkojote.library.domain.model.work.events.AuthorRemovedEvent;
import com.jkojote.library.domain.model.work.events.SubjectAddedEvent;
import com.jkojote.library.domain.model.work.events.SubjectRemovedEvent;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.domain.shared.DomainArrayList;
import com.jkojote.library.domain.shared.Utils;
import com.jkojote.library.domain.shared.domain.DomainList;
import com.jkojote.library.domain.shared.values.DateRange;

import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkNotNull;

public class Work extends DomainEntity {

    private String title;

    private DateRange appeared;

    private DomainList<Author> authors;

    private DomainList<Subject> subjects;

    private Work(long id,
                 String title,
                 DateRange appeared,
                 Author author) {
        super(id);
        this.title    = title;
        this.authors  = new DomainArrayList<>();
        this.subjects = new DomainArrayList<>();
        this.appeared = appeared;
        this.authors.add(author);
    }

    private Work(long id, String title,
                 DateRange appeared,
                 DomainList<Author> authors,
                 DomainList<Subject> subjects) {
        super(id);
        this.title    = title;
        this.authors  = authors;
        this.appeared = appeared;
        this.subjects = subjects;
    }

    public static Work create(long id, String title, Author author, DateRange appeared) {
        checkNotNull(title);
        checkNotNull(author);
        Work work = new Work(id, title, appeared, author);
        author.addWork(work);
        return work;
    }

    public static Work create(long id, String title, DomainList<Author> authors, DateRange appeared) {
        checkNotNull(title);
        checkNotNull(authors);
        Work work = new Work(id, title, appeared, authors, new DomainArrayList<>());
        for (var author : authors)
            author.addWork(work);
        return work;
    }

    public static Work restore(long id, String title,
                               DateRange appeared,
                               DomainList<Author> authors,
                               DomainList<Subject> subjects) {
        checkNotNull(authors);
        checkNotNull(title);
        checkNotNull(subjects);
        return new Work(id, title, appeared, authors, subjects);
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
            author.removeWork(this);
            notifyAllListeners(new AuthorRemovedEvent(this, author, null));
            return true;
        }
        return false;
    }

    public boolean addAuthor(Author author) {
        if (authors.contains(author))
            return false;
        authors.add(author);
        if (!author.getWorks().contains(this)) {
            author.addWork(this);
            notifyAllListeners(new AuthorAddedEvent(this, author, null));
            return true;
        }
        return false;
    }

    public DateRange whenAppeared() {
        return appeared;
    }

    public String getTitle() {
        return title;
    }

}
