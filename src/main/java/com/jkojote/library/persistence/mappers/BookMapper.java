package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.publisher.PublisherRepository;
import com.jkojote.library.domain.model.work.WorkRepository;
import com.jkojote.library.persistence.fetchers.LazyBookInstancesListFetcher;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class BookMapper implements RowMapper<Book> {

    private WorkRepository workRepository;

    private PublisherRepository publisherRepository;

    private LazyBookInstancesListFetcher listFetcher;

    @Autowired
    public BookMapper(WorkRepository workRepository,
                      PublisherRepository publisherRepository) {
        this.workRepository = workRepository;
        this.publisherRepository = publisherRepository;
    }

    @Autowired
    public void setListFetcher(LazyBookInstancesListFetcher listFetcher) {
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
