package com.jkojote.library.persistence.fetchers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.reader.Rating;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.ListFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("readerRatingsFetcher")
@Transactional
class LazyReaderRatingFetcher implements ListFetcher<Reader, Rating> {

    private static final String SELECT =
        "SELECT * FROM Rating WHERE readerId = ?";

    private JdbcTemplate jdbcTemplate;

    private DomainRepository<Book> bookRepository;

    @Autowired
    public LazyReaderRatingFetcher(JdbcTemplate jdbcTemplate,
                                   @Qualifier("bookRepository")
                                   DomainRepository<Book> bookRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Rating> fetchFor(Reader reader) {
        return jdbcTemplate.query(SELECT, (rs, rn) -> {
            int rating = rs.getInt("rating");
            long bookId = rs.getLong("bookId");
            Book book = bookRepository.findById(bookId);
            return new Rating(reader, book, rating);
        }, reader.getId());
    }
}
