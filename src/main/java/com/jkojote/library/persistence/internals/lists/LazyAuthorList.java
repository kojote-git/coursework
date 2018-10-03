package com.jkojote.library.persistence.internals.lists;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.AbstractLazyList;
import com.jkojote.library.persistence.ListFetcher;

public class LazyAuthorList extends AbstractLazyList<Work, Author> {

    public LazyAuthorList(Work entity, ListFetcher<Work, Author> fetcher) {
        super(entity, fetcher);
    }

    public LazyAuthorList(ListFetcher<Work, Author> fetcher) {
        super(fetcher);
    }
}
