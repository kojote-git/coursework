package com.jkojote.library.persistence.lazy;

import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.files.FileInstance;
import com.jkojote.library.persistence.LazyObject;
import com.jkojote.library.persistence.LazyObjectFetcher;
import com.jkojote.library.persistence.Refreshable;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

public class LazyFileInstance<T extends DomainEntity>
implements FileInstance, LazyObject<FileInstance>, Refreshable<FileInstance> {

    private LazyObjectFetcher<T, byte[]> fetcher;

    private T parentEntity;

    private byte[] file;

    private boolean fetched;

    private boolean sealed;

    public LazyFileInstance(T parentEntity, LazyObjectFetcher<T, byte[]> fetcher) {
        this.parentEntity = parentEntity;
        this.fetcher = fetcher;
        this.fetched = false;
    }

    public LazyFileInstance(LazyObjectFetcher<T, byte[]> fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public byte[] asBytes() {
        fetch();
        return this.file;
    }

    @Override
    public byte[] asBytes(int length) {
        fetch();
        return Arrays.copyOf(file, length);
    }

    @Override
    public int length() {
        fetch();
        return this.file.length;
    }

    @Override
    public Blob asBlob() {
        fetch();
        try {
            return new SerialBlob(this.file);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isFetched() {
        return fetched;
    }

    private void fetch() {
        if (!fetched) {
            this.file = fetcher.fetchFor(parentEntity);
            this.fetched = true;
        }
    }

    public void setParentEntity(T parentEntity) {
        if (!sealed)
            this.parentEntity = parentEntity;
        else
            throw new IllegalStateException("file instance cannot be modified as it's sealed");
    }

    public void seal() {
        if (!sealed)
            sealed = true;
    }

    @Override
    public FileInstance get() {
        fetch();
        return this;
    }

    @Override
    public void refresh() {
        if (!fetched)
            fetched = true;
        this.file = fetcher.fetchFor(parentEntity);
    }
}
