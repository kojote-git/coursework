package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@SuppressWarnings("ALL")
@Component
@Transactional
public class AuthorTableProcessor implements TableProcessor<Author> {

    private static final String INSERT =
        "INSERT INTO Author (id, firstName, middleName, lastName) "+
          "VALUES (?, ?, ? , ?)";

    private static final String UPDATE =
        "UPDATE Author SET firstName = ?, middleName = ?, lastName = ? WHERE id = ?";

    private static final String DELETE =
        "DELETE FROM Author WHERE id = ?";

    private static final String EXISTS_QUERY =
        "SELECT COUNT(id) FROM Author WHERE id = ?";

    private JdbcTemplate jdbcTemplate;

    private static final int MAX_CACHE_SIZE = 256;

    private final Set<Long> cache = new ConcurrentSkipListSet<>();

    @Autowired
    public AuthorTableProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean exists(Author e) {
        if (e == null)
            return false;
        if (cache.contains(e.getId()))
            return true;
        var count = jdbcTemplate.queryForObject(EXISTS_QUERY, (rs, rn) -> rs.getLong(1), e.getId());
        var res = count != null && count == 1;
        if (res && cache.size() < MAX_CACHE_SIZE)
            cache.add(e.getId());
        return res;
    }

    @Override
    public boolean insert(Author e) {
        if (e == null)
            return false;
        if (exists(e))
            return false;
        jdbcTemplate.update(INSERT, e.getId(),
                e.getName().getFirstName(),
                e.getName().getMiddleName(),
                e.getName().getLastName());
        return true;
    }

    @Override
    public boolean delete(Author e) {
        if (e == null)
            return false;
        if (!exists(e))
            return false;
        cache.remove(e.getId());
        jdbcTemplate.update(DELETE, e.getId());
        return true;
    }

    @Override
    public boolean update(Author e) {
        if (e == null)
            return false;
        if (!exists(e))
            return false;
        jdbcTemplate.update(UPDATE, e.getName().getFirstName(),
                e.getName().getMiddleName(),
                e.getName().getLastName(),
                e.getId());
        return true;
    }
}
