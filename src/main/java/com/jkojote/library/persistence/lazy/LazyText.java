package com.jkojote.library.persistence.lazy;

import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.persistence.LazyObject;
import com.jkojote.library.persistence.LazyObjectFetcher;
import com.jkojote.library.values.Text;

public class LazyText<T extends DomainEntity>
extends Text implements LazyObject<Text> {

    private LazyObjectFetcher<T, Text> fetcher;

    private T parent;

    private Text text;

    public LazyText(T parent, LazyObjectFetcher<T, Text> fetcher) {
        this.fetcher = fetcher;
        this.parent = parent;
    }

    @Override
    public boolean isFetched() {
        return text != null;
    }

    @Override
    public Text get() {
        if (!isFetched()) {
            text = fetcher.fetchFor(parent);
        }
        return text;
    }

    @Override
    public String toString() {
        return get().toString();
    }

    @Override
    public int length() {
        return get().length();
    }
}
