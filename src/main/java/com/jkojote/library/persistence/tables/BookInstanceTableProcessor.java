package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.files.FileInstance;
import com.jkojote.library.persistence.LazyObject;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Component("bookInstanceTable")
@Transactional
@SuppressWarnings("Duplicates")
class BookInstanceTableProcessor implements TableProcessor<BookInstance> {

    private static final String INSERT =
        "INSERT INTO BookInstance (id, bookId, isbn13, file, format, cover) " +
          "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_WITHOUT_FILE =
        "UPDATE BookInstance SET bookId = ?, isbn13 = ?, format = ? WHERE id = ?";

    private static final String UPDATE_WITH_FILE =
        "UPDATE BookInstance SET bookId = ?, isbn13 = ?, format = ?, file = ?, cover = ? WHERE id = ?";

    private static final String DELETE =
        "DELETE FROM BookInstance WHERE id = ?";

    private static final String QUERY_EXISTS =
        "SELECT COUNT(*) FROM BookInstance WHERE id = ?";

    private static final int CACHE_MAX_SIZE = 256;

    private Set<Long> cache = new ConcurrentSkipListSet<>();

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BookInstanceTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean exists(BookInstance e) {
        if (e == null)
            return false;
        if (cache.contains(e.getId()))
            return true;
        Long count = jdbcTemplate.queryForObject(QUERY_EXISTS, (rs, rn) -> rs.getLong(1), e.getId());
        boolean res = count != null && count == 1;
        if (res)
            tryPutToCache(e.getId());
        return res;
    }

    @Override
    public boolean insert(BookInstance e) {
        if (e == null)
            return false;
        if (exists(e))
            return false;
        tryPutToCache(e.getId());
        jdbcTemplate.update(INSERT, e.getId(),
                e.getBook().getId(),
                e.getIsbn13().asString(),
                e.getFile().asBlob(),
                e.getFormat().asString(),
                e.getCover().asBlob());
        return true;
    }

    @Override
    public boolean delete(BookInstance e) {
        if (!exists(e))
            return false;
        cache.remove(e.getId());
        jdbcTemplate.update(DELETE, e.getId());
        return true;
    }

    @Override
    public boolean update(BookInstance e) {
        if (!exists(e))
            return false;
        FileInstance file = e.getFile();
        boolean fileIsLazy = file instanceof LazyObject;
        if (!fileIsLazy || ((LazyObject)file).isFetched())
            jdbcTemplate.update(UPDATE_WITH_FILE, e.getBook().getId(),
                    e.getIsbn13().asString(),
                    e.getFormat().asString(),
                    e.getFile().asBlob(),
                    e.getCover().asBlob(),
                    e.getId()
            );
        else
            jdbcTemplate.update(UPDATE_WITHOUT_FILE, e.getBook().getId(),
                    e.getIsbn13().asString(),
                    e.getFormat().asString(),
                    e.getId()
            );
        return true;
    }

    @Override
    public void batchInsert(Collection<BookInstance> c) {

    }

    private void tryPutToCache(long value) {
        if (cache.size() < CACHE_MAX_SIZE)
            cache.add(value);
    }
}
