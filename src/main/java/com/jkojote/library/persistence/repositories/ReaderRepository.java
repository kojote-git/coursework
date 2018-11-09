package com.jkojote.library.persistence.repositories;

import com.jkojote.library.clauses.SqlClause;
import com.jkojote.library.domain.model.reader.Download;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.domain.shared.domain.FilteringAndSortingRepository;
import com.jkojote.library.persistence.MapCache;
import com.jkojote.library.persistence.MapCacheImpl;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository("readerRepository")
@Transactional
class ReaderRepository implements FilteringAndSortingRepository<Reader> {

    private MapCache<Long, Reader> cache;

    private TableProcessor<Reader> readerTable;

    private TableProcessor<Download> downloadTable;

    private RowMapper<Reader> readerMapper;

    private JdbcTemplate jdbcTemplate;

    private DomainEventListener<Reader> readerStateListener;

    private AtomicLong lastId;

    @Autowired
    public ReaderRepository(
            JdbcTemplate jdbcTemplate,
            @Qualifier("readerTable")
            TableProcessor<Reader> readerTable,
            @Qualifier("downloadTable")
            TableProcessor<Download> downloadTable,
            @Qualifier("readerMapper")
            RowMapper<Reader> readerMapper,
            @Qualifier("readerStateListener")
            DomainEventListener<Reader> readerStateListener) {
        this.readerTable = readerTable;
        this.jdbcTemplate = jdbcTemplate;
        this.cache = new MapCacheImpl<>();
        this.readerMapper = readerMapper;
        this.downloadTable = downloadTable;
        this.readerStateListener = readerStateListener;
        this.cache.disable();
        initLastId();
    }

    @Override
    public Reader findById(long id) {
        Reader reader = cache.get(id);
        if (reader != null)
            return reader;
        try {
            String query = "SELECT * FROM Reader WHERE id = ?";
            reader = jdbcTemplate.queryForObject(query, readerMapper, id);
            cache.put(id, reader);
            return reader;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public List<Reader> findAll() {
        return jdbcTemplate.query("SELECT * FROM Reader", readerMapper);
    }

    @Override
    public long nextId() {
        return lastId.incrementAndGet();
    }

    @Override
    public boolean exists(Reader entity) {
        return readerTable.exists(entity);
    }

    @Override
    public boolean save(Reader reader) {
        if (exists(reader))
            return false;
        readerTable.insert(reader);
        downloadTable.batchInsert(reader.getDownloads());
        reader.addEventListener(readerStateListener);
        return true;
    }

    @Override
    public boolean remove(Reader reader) {
        if (!exists(reader))
            return false;
        cache.remove(reader.getId());
        readerTable.delete(reader);
        reader.removeListener(readerStateListener);
        return true;
    }

    @Override
    public boolean update(Reader reader) {
        return readerTable.update(reader);
    }

    private void initLastId() {
        long last = jdbcTemplate.queryForObject("SELECT MAX(id) FROM Reader", (rs, rn) -> {
            return rs.getLong(1);
        });
        lastId = new AtomicLong(last);
    }

    @Override
    public List<Reader> findAll(SqlClause clause) {
        return jdbcTemplate.query("SELECT * FROM Reader " + clause.asString(), readerMapper);
    }
}
