package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.reader.Download;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.ListFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component("downloadsFetcher")
@Transactional
class LazyReaderDownloadFetcher implements ListFetcher<Reader, Download> {

    private DomainRepository<BookInstance> bookInstanceRepository;

    private static final String QUERY =
        "SELECT * FROM Download WHERE readerId = ?";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LazyReaderDownloadFetcher(JdbcTemplate jdbcTemplate,
                                     @Qualifier("bookInstanceRepository")
                                     DomainRepository<BookInstance> bookInstanceRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookInstanceRepository = bookInstanceRepository;
    }

    @Override
    public List<Download> fetchFor(Reader reader) {
        return jdbcTemplate.query(QUERY, (rs, rn) -> {
            long bookInstanceId = rs.getLong("bookInstanceId");
            BookInstance bi = bookInstanceRepository.findById(bookInstanceId);
            int rating = rs.getInt("readerRating");
            LocalDateTime dateDownloaded = rs.getTimestamp("dateDownloaded").toLocalDateTime();
            return new Download(reader, bi, dateDownloaded, rating);
        }, reader.getId());
    }
}
