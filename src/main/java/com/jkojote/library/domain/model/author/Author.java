package com.jkojote.library.domain.model.author;

import com.jkojote.library.domain.model.author.events.WorkAddedEvent;
import com.jkojote.library.domain.model.author.events.WorkRemovedEvent;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.DomainEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class Author extends DomainEntity {

    private Name name;

    private List<Work> works;

    private Author(int id, Name name, List<Work> works) {
        super(id);
        this.name = name;
        this.works = works;
    }

    public static Author createNew(int id, Name name) {
        checkNotNull(name);
        return new Author(id, name, new ArrayList<>());
    }

    public static Author restore(int id, Name name, List<Work> works) {
        checkNotNull(name);
        checkNotNull(works);
        return new Author(id, name, new ArrayList<>(works));
    }

    public List<Work> getWorks() {
        return Collections.unmodifiableList(works);
    }

    public Name getName() {
        return name;
    }

    public List<Work> getWork(Predicate<Work> criteria) {
        return works.stream()
                .filter(criteria)
                .collect(Collectors.toList());
    }

    public boolean addWork(Work work) {
        if (works.contains(work))
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
}
