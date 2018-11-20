package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.persistence.TableProcessor;
import com.neovisionaries.i18n.LanguageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Component("bookTable")
@Transactional
@SuppressWarnings("Duplicates")
class BookTableProcessor implements TableProcessor<Book> {

    private static final String INSERT =
        "INSERT INTO Book (id, workId, publisherId, edition, title, lang) " +
          "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
        "UPDATE Book SET workId = ?, publisherId = ?, edition = ?, title = ?, lang = ? WHERE id = ?";

    private static final String DELETE =
        "DELETE FROM Book WHERE id = ?";

    private static final String QUERY_EXISTS =
        "SELECT COUNT(*) FROM Book WHERE id = ?";

    private static final int CACHE_MAX_SIZE = 512;

    private final Set<Long> cache = new ConcurrentSkipListSet<>();

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BookTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean exists(Book e) {
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
    public boolean insert(Book e) {
        if (e == null)
            return false;
        if (exists(e))
            return false;
        tryPutToCache(e.getId());
        String lang = e.getLanguage() == LanguageCode.undefined ? "" : e.getLanguage().toString();
        jdbcTemplate.update(INSERT, e.getId(),
                e.getBasedOn().getId(),
                e.getPublisher().getId(),
                e.getTitle(), lang,
                e.getEdition());
        return true;
    }

    @Override
    public boolean delete(Book e) {
        if (!exists(e))
            return false;
        cache.remove(e.getId());
        jdbcTemplate.update(DELETE, e.getId());
        return true;
    }

    private void tryPutToCache(long value) {
        if (cache.size() < CACHE_MAX_SIZE)
            cache.add(value);
    }

    @Override
    public boolean update(Book e) {
        if (!exists(e))
            return false;
        String lang = e.getLanguage() == LanguageCode.undefined ? "" : e.getLanguage().toString();
        jdbcTemplate.update(UPDATE, e.getBasedOn().getId(),
                e.getPublisher().getId(),
                e.getEdition(),
                e.getTitle(), lang,
                e.getId());
        return true;
    }
}
