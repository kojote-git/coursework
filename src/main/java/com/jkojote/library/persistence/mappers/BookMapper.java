package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component("bookMapper")
class BookMapper implements RowMapper<Book> {

    private DomainRepository<Work> workRepository;

    private DomainRepository<Publisher> publisherRepository;

    private ListFetcher<Book, BookInstance> listFetcher;

    private DomainEventListener<Book> bookStateListener;

    @Autowired
    public BookMapper(@Qualifier("bookStateListener")
                      DomainEventListener<Book> bookStateListener) {
        this.bookStateListener = bookStateListener;
    }

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
        long id = rs.getLong("id");
        long publisherId = rs.getLong("publisherId");
        long workId = rs.getLong("workId");
        int edition = rs.getInt("edition");
        Work work = workRepository.findById(workId);
        Publisher publisher = publisherRepository.findById(publisherId);
        LazyListImpl<Book, BookInstance> list = new LazyListImpl<>(listFetcher);
        Book book = new Book(id, work, publisher, edition, list);
        list.setParentEntity(book);
        list.seal();
        book.addEventListener(bookStateListener);
        return book;
    }
}
