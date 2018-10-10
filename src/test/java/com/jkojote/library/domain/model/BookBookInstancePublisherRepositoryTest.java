package com.jkojote.library.domain.model;

import com.jkojote.library.config.PersistenceConfig;
import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookFormat;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.book.instance.isbn.Isbn13;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.DomainArrayList;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.files.StandardFileInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfig.class)
@DirtiesContext
public class BookBookInstancePublisherRepositoryTest implements InitializingBean {

    private Book book;

    private Publisher publisher;

    private BookInstance bi1;

    private BookInstance bi2;

    @Autowired
    private DomainRepository<Work> workRepository;

    @Autowired
    private DomainRepository<BookInstance> bookInstanceRepository;

    @Autowired
    private DomainRepository<Publisher> publisherRepository;

    @Autowired
    private DomainRepository<Book> bookRepository;

    @Test(expected = NullPointerException.class)
    public void save() {
        bookRepository.save(book);
    }

    private void initPublisher() {
        this.publisher = new Publisher(4, "Imaginary publisher", new DomainArrayList<>());
    }

    private void initBook() {
        // imaginary work
        Work work = workRepository.findById(5);
        // when saving book, NullPointerException is thrown to test
        // transaction management works and it then rolls back
        this.book = new Book(bookRepository.nextId(), null, publisher, 1, new DomainArrayList<>());

        var file1 = new StandardFileInstance("src/main/resources/file1.txt");
        var file2 = new StandardFileInstance("src/main/resources/file2.pdf");
        bi1 = new BookInstance(bookInstanceRepository.nextId(),
                book, Isbn13.of("978-90-1235-321-1"), BookFormat.TXT, file1);
        bi2 = new BookInstance(bookInstanceRepository.nextId(),
                book, Isbn13.of("978-91-1235-321-1"), BookFormat.PDF, file2);
        book.addBookInstance(bi1);
        book.addBookInstance(bi2);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initPublisher();
        initBook();
    }
}
