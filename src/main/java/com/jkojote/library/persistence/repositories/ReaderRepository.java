package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.MapCache;
import com.jkojote.library.persistence.MapCacheImpl;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("readerRepository")
@Transactional
class ReaderRepository implements DomainRepository<Reader> {

    private MapCache<Long, Reader> cache;

    private TableProcessor<Reader> readerTable;

    @Autowired
    public ReaderRepository(
            @Qualifier("readerTable")
            TableProcessor<Reader> readerTable) {
        this.readerTable = readerTable;
        this.cache = new MapCacheImpl<>();
        this.cache.disable();
    }

    @Override
    public Reader findById(long id) {
        return null;
    }

    @Override
    public List<Reader> findAll() {
        return null;
    }

    @Override
    public long nextId() {
        return 0;
    }

    @Override
    public boolean exists(Reader entity) {
        return false;
    }

    @Override
    public boolean save(Reader entity) {
        return false;
    }

    @Override
    public boolean remove(Reader entity) {
        return false;
    }

    @Override
    public boolean update(Reader entity) {
        return false;
    }
}
