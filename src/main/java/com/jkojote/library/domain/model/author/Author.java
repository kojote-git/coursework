package com.jkojote.library.domain.model.author;

import com.jkojote.library.domain.model.author.events.WorkAddedEvent;
import com.jkojote.library.domain.model.author.events.WorkRemovedEvent;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.domain.shared.domain.Required;
import com.jkojote.library.values.Name;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Author extends DomainEntity {

    private Name name;

    private List<Work> works;

    private Author(long id, Name name, List<Work> works) {
        super(id);
        this.name = name;
        this.works = works;
    }

    @Deprecated
    public static Author createNew(long id, Name name) {
        checkNotNull(name);
        return new Author(id, name, new ArrayList<>());
    }

    @Deprecated
    public static Author restore(long id, Name name, List<Work> works) {
        checkNotNull(name);
        checkNotNull(works);
        return new Author(id, name, works);
    }

    public List<Work> getWorks() {
        return Collections.unmodifiableList(works);
    }

    public void setName(Name name) {
        checkNotNull(name);
        this.name = name;
    }

    public Name getName() {
        return name;
    }

    public List<Work> filterWorks(Predicate<Work> criteria) {
        return works.stream()
                .filter(criteria)
                .collect(Collectors.toList());
    }

    public boolean addWork(Work work) {
        if (work == null || works.contains(work))
            return false;
        works.add(work);
        if (!work.getAuthors().contains(this)) {
            work.addAuthor(this);
        }
        notifyAllListeners(new WorkAddedEvent(this, work, null));
        return true;
    }

    public boolean removeWork(Work work) {
        if (!works.contains(work))
            return false;
        works.remove(work);
        if (work.getAuthors().contains(this)) {
            work.removeAuthor(this);
        }
        notifyAllListeners(new WorkRemovedEvent(this, work, null));
        return true;
    }

    public static final class AuthorBuilder {

        private long id;

        private Name name;

        private List<Work> works;

        private AuthorBuilder() {
        }

        public static AuthorBuilder anAuthor() {
            return new AuthorBuilder();
        }

        @Required
        public AuthorBuilder withId(long id) {
            checkArgument(id > 0);
            this.id = id;
            return this;
        }

        public AuthorBuilder withName(Name name) {
            checkNotNull(name);
            this.name = name;
            return this;
        }

        public AuthorBuilder withWorks(List<Work> works) {
            checkNotNull(works);
            this.works = works;
            return this;
        }

        public Author build() {
            if (id == 0)
                throw new IllegalStateException("id must be set for author");
            if (name == null)
                name = Name.EMPTY;
            if (works == null)
                works = new ArrayList<>();
            return new Author(id, name, works);
        }
    }
}
