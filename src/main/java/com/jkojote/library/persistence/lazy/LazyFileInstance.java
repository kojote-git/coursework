package com.jkojote.library.persistence.lazy;

import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.files.FileInstance;
import com.jkojote.library.persistence.LazyObject;
import com.jkojote.library.persistence.LazyObjectFetcher;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

public class LazyFileInstance<T extends DomainEntity>
implements FileInstance, LazyObject<FileInstance> {

    private LazyObjectFetcher<T, byte[]> fetcher;

    private T parentEntity;

    private byte[] file;

    private boolean fetched;

    public LazyFileInstance(T parentEntity, LazyObjectFetcher<T, byte[]> fetcher) {
        this.parentEntity = parentEntity;
        this.fetcher = fetcher;
        this.fetched = false;
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

    @Override
    public FileInstance get() {
        fetch();
        return this;
    }
}
