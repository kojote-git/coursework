package com.jkojote.library.domain.model.author;

import com.jkojote.library.domain.shared.*;
import com.jkojote.library.domain.model.author.events.WorkAddedEvent;
import com.jkojote.library.domain.model.author.events.WorkRemovedEvent;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.domain.shared.domain.DomainList;
import com.jkojote.library.values.Name;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class Author extends DomainEntity {

    private Name name;

    private DomainList<Work> works;

    private Author(long id, Name name, DomainList<Work> works) {
        super(id);
        this.name = name;
        this.works = works;
    }

    public static Author createNew(long id, Name name) {
        checkNotNull(name);
        return new Author(id, name, new DomainArrayList<>());
    }

    public static Author restore(long id, Name name, DomainList<Work> works) {
        checkNotNull(name);
        checkNotNull(works);
        return new Author(id, name, works);
    }

    public DomainList<Work> getWorks() {
        return Utils.unmodifiableDomainList(works);
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
