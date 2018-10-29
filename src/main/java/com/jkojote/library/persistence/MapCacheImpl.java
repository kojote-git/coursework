package com.jkojote.library.persistence;

import com.jkojote.library.domain.shared.domain.DomainEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;

public class MapCacheImpl<T extends DomainEntity> implements MapCache<Long, T> {

    private Map<Long, T> cache;

    private boolean disabled = false;

    private int maxCapacity;

    public MapCacheImpl() {
        this.cache = new ConcurrentHashMap<>();
        maxCapacity = Integer.MAX_VALUE;
    }

    public MapCacheImpl(int maxCapacity) {
        checkArgument(maxCapacity > 0);
        this.cache = new ConcurrentHashMap<>();
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean contains(Long k) {
        if (disabled)
            return false;
        return cache.containsKey(k);
    }

    @Override
    public boolean put(Long id, T e) {
        if (disabled)
            return false;
        cache.put(id, e);
        return true;
    }

    @Override
    public T get(Long id) {
        if (disabled)
            return null;
        return cache.get(id);
    }

    @Override
    public boolean remove(Long id) {
        if (disabled)
            return false;
        return cache.remove(id) == null;
    }

    @Override
    public synchronized void disable() {
        disabled = true;
    }

    @Override
    public synchronized void enable() {
        disabled = false;
    }

    @Override
    public void clean() {
        if (disabled)
            return;
        cache.clear();
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public int maxCapacity() {
        return maxCapacity;
    }

    @Override
    public int size() {
        return cache.size();
    }
}
