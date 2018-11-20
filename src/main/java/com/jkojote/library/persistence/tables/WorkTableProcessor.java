package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.LazyObject;
import com.jkojote.library.persistence.TableProcessor;
import com.jkojote.library.values.Text;
import com.neovisionaries.i18n.LanguageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@SuppressWarnings("ALL")
@Component("workTable")
@Transactional
class WorkTableProcessor implements TableProcessor<Work> {

    private static final String INSERT =
        "INSERT INTO Work (id, title, lang, description) "+
          "VALUES (?, ?, ?, ?)";

    private static final String UPDATE =
        "UPDATE Work SET title = ?, lang = ? WHERE id = ?";

    private static final String UPDATE_WITH_DESCRIPTION =
        "UPDATE Work SET title = ?, lang = ?, description = ? WHERE id = ?";

    private static final String DELETE =
        "DELETE FROM Work WHERE id = ?";

    private static final String EXISTS_QUERY =
        "SELECT COUNT(id) FROM Work WHERE id = ?";

    private static final int CACHE_MAX_SIZE = 256;

    private final Set<Long> cache = new ConcurrentSkipListSet<>();

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public WorkTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean exists(Work e) {
        if (e == null)
            return false;
        if (cache.contains(e.getId()))
            return true;
        Long count = jdbcTemplate.queryForObject(EXISTS_QUERY, (rs, rn) -> rs.getLong(1), e.getId());
        boolean res = count != null && count == 1;
        if (res)
            tryPutToCache(e.getId());
        return res;
    }

    @Override
    public boolean insert(Work e) {
        if (e == null)
            return false;
        if (exists(e))
            return false;
        tryPutToCache(e.getId());
        String lang = e.getLanguage() == LanguageCode.undefined ? "" : e.getLanguage().toString();
        jdbcTemplate.update(INSERT,
                e.getId(), e.getTitle(), lang,
                e.getDescription().toString());
        return true;
    }

    @Override
    public boolean delete(Work e) {
        if (!exists(e))
            return false;
        cache.remove(e.getId());
        jdbcTemplate.update(DELETE, e.getId());
        return true;
    }

    @Override
    public boolean update(Work e) {
        if (!exists(e))
            return false;
        Text description = e.getDescription();
        String lang = e.getLanguage() == LanguageCode.undefined ? "" : e.getLanguage().toString();
        boolean isFetched = !(description instanceof LazyObject) || ((LazyObject) description).isFetched();
        if (isFetched)
            jdbcTemplate.update(UPDATE_WITH_DESCRIPTION, e.getTitle(), lang, description.toString(), e.getId());
        else
            jdbcTemplate.update(UPDATE, e.getTitle(), lang, e.getId());
        return true;
    }

    private void tryPutToCache(long value) {
        if (cache.size() < CACHE_MAX_SIZE)
            cache.add(value);
    }
}




