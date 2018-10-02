package com.jkojote.library.persistence.internals.lists;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.AbstractLazyList;
import com.jkojote.library.persistence.ListFetcher;

public class LazyWorkList extends AbstractLazyList<Author, Work> {

    protected LazyWorkList(Author entity, ListFetcher<Author, Work> fetcher) {
        super(entity, fetcher);
    }
}
