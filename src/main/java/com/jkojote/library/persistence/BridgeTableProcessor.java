package com.jkojote.library.persistence;

public interface BridgeTableProcessor<T1, T2> {

    boolean removeRecord(T1 t1, T2 t2);

    boolean addRecord(T1 t1, T2 t2);

    boolean exists(T1 t1, T2 t2);
}
