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

import static com.google.common.base.Preconditions.checkNotNull;

public class Work extends DomainEntity {

    private String title;

    private String description;

    private DomainList<Author> authors;

    private DomainList<Subject> subjects;

    private Work(long id,
                 String title,
                 Author author) {
        super(id);
        this.title    = title;
        this.authors  = new DomainArrayList<>();
        this.subjects = new DomainArrayList<>();
        this.authors.add(author);
    }

    private Work(long id, String title,
                 DomainList<Author> authors,
                 DomainList<Subject> subjects) {
        super(id);
        this.title    = title;
        this.authors  = authors;
        this.subjects = subjects;
    }

    public static Work create(long id, String title, Author author) {
        checkNotNull(title);
        checkNotNull(author);
        Work work = new Work(id, title, author);
        author.addWork(work);
        return work;
    }

    public static Work create(long id, String title, DomainList<Author> authors) {
        checkNotNull(title);
        checkNotNull(authors);
        Work work = new Work(id, title, authors, new DomainArrayList<>());
        for (Author author : authors)
            author.addWork(work);
        return work;
    }

    public static Work restore(long id, String title,
                               DomainList<Author> authors,
                               DomainList<Subject> subjects) {
        checkNotNull(authors);
        checkNotNull(title);
        checkNotNull(subjects);
        return new Work(id, title, authors, subjects);
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
        if (author == null || !authors.contains(author))
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
        if (author == null || authors.contains(author))
            return false;
        authors.add(author);
        if (!author.getWorks().contains(this)) {
            author.addWork(this);
            notifyAllListeners(new AuthorAddedEvent(this, author, null));
            return true;
        }
        return false;
    }

    public void setDescription(String description) {
        checkNotNull(description);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void changeTitle(String newTitle) {
        checkNotNull(newTitle);
        this.title = newTitle;
    }

    public String getTitle() {
        return title;
    }

}
