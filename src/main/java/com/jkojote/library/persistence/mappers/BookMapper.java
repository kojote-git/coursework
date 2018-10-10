package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.fetchers.LazyBookInstancesListFetcher;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class BookMapper implements RowMapper<Book> {

    private DomainRepository<Work> workRepository;

    private DomainRepository<Publisher> publisherRepository;

    private ListFetcher<Book, BookInstance> listFetcher;

    @Autowired
    public void setPublisherRepository(DomainRepository<Publisher> publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Autowired
    public void setWorkRepository(DomainRepository<Work> workRepository) {
        this.workRepository = workRepository;
    }

    @Autowired
    public void setListFetcher(ListFetcher<Book, BookInstance> listFetcher) {
        this.listFetcher = listFetcher;
    }

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        var id = rs.getLong("id");
        var publisherId = rs.getLong("publisherId");
        var workId = rs.getLong("workId");
        var edition = rs.getInt("edition");
        var work = workRepository.findById(workId);
        var publisher = publisherRepository.findById(publisherId);
        var list = new LazyListImpl<>(listFetcher);
        return new Book(id, work, publisher, edition, list);
    }
}
