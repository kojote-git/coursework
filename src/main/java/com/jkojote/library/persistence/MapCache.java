package com.jkojote.library.persistence;

public interface MapCache<K, V> {

    boolean contains(K k);

    boolean put(K k, V v);

    V get(K k);

    boolean remove(K key);

    void disable();

    void enable();

    void clean();

    boolean isDisabled();

    int maxCapacity();

    int size();
}
