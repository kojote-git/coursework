package com.jkojote.library.domain.model.work;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.events.AuthorAddedEvent;
import com.jkojote.library.domain.model.work.events.AuthorRemovedEvent;
import com.jkojote.library.domain.model.work.events.SubjectAddedEvent;
import com.jkojote.library.domain.model.work.events.SubjectRemovedEvent;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.domain.shared.domain.Required;
import com.jkojote.library.values.OrdinaryText;
import com.jkojote.library.values.Text;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Work extends DomainEntity {

    private String title;

    private Text description;

    private List<Author> authors;

    private List<Subject> subjects;

    private Work(long id,
                 String title,
                 Author author) {
        super(id);
        this.title    = title;
        this.authors  = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.authors.add(author);
        this.description = OrdinaryText.EMPTY;
    }

    private Work(long id, String title,
                 List<Author> authors,
                 List<Subject> subjects) {
        super(id);
        this.title    = title;
        this.authors  = authors;
        this.subjects = subjects;
        this.description = OrdinaryText.EMPTY;
    }

    @Deprecated
    public static Work create(long id, String title, Author author) {
        checkNotNull(title);
        checkNotNull(author);
        return new Work(id, title, author);
    }

    @Deprecated
    public static Work create(long id, String title, List<Author> authors) {
        checkNotNull(title);
        checkNotNull(authors);
        return new Work(id, title, authors, new ArrayList<>());
    }

    @Deprecated
    public static Work restore(long id, String title,
                               List<Author> authors,
                               List<Subject> subjects) {
        checkNotNull(authors);
        checkNotNull(title);
        checkNotNull(subjects);
        return new Work(id, title, authors, subjects);
    }

    public List<Author> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public List<Subject> getSubjects() {
        return Collections.unmodifiableList(subjects);
    }

    public boolean addSubject(Subject subject) {
        if (subjects == null || subjects.contains(subject))
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
        }
        notifyAllListeners(new AuthorRemovedEvent(this, author, null));
        return true;
    }

    public boolean addAuthor(Author author) {
        if (author == null || authors.contains(author))
            return false;
        authors.add(author);
        if (!author.getWorks().contains(this)) {
            author.addWork(this);
        }
        notifyAllListeners(new AuthorAddedEvent(this, author, null));
        return true;
    }

    public void setDescription(Text description) {
        checkNotNull(description);
        this.description = description;
    }

    public Text getDescription() {
        return description;
    }

    public void changeTitle(String newTitle) {
        checkNotNull(newTitle);
        this.title = newTitle;
    }

    public String getTitle() {
        return title;
    }

    public static final class WorkBuilder {

        private long id;

        private String title;

        private Text description;

        private List<Author> authors;

        private List<Subject> subjects;

        private WorkBuilder() {
        }

        public static WorkBuilder aWork() {
            return new WorkBuilder();
        }

        @Required
        public WorkBuilder withId(long id) {
            checkArgument(id > 0);
            this.id = id;
            return this;
        }

        @Required
        public WorkBuilder withTitle(String title) {
            checkNotNull(title, "title must not be null");
            this.title = title;
            return this;
        }

        public WorkBuilder addAuthor(Author a) {
            if (authors == null)
                authors = new LinkedList<>();
            if (!authors.contains(a))
                this.authors.add(a);
            return this;
        }

        public WorkBuilder withDescription(Text description) {
            if (description == null)
                this.description = OrdinaryText.EMPTY;
            else
                this.description = description;
            return this;
        }

        public WorkBuilder withAuthors(List<Author> authors) {
            checkNotNull(authors);
            if (this.authors != null)
                this.authors.addAll(authors);
            else
                this.authors = authors;
            return this;
        }

        public WorkBuilder withSubjects(List<Subject> subjects) {
            if (subjects == null)
                this.subjects = new LinkedList<>();
            else
                this.subjects = subjects;
            return this;
        }

        public Work build() {
            if (id == 0)
                throw new IllegalStateException("id must be set for work");
            checkNotNull(title, "title must not be null");
            checkNotNull(authors);
            if (description == null)
                description = OrdinaryText.EMPTY;
            if (subjects == null)
                subjects = new LinkedList<>();
            Work work = new Work(id, title, authors, subjects);
            work.description = description;
            return work;
        }
    }
}
