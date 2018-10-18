package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookFormat;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.book.instance.isbn.Isbn13;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.DomainArrayList;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.files.FileInstance;
import com.jkojote.library.files.StandardFileInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class BookBookInstancePublisherRepositoryTest implements InitializingBean {

    private Book book;

    private Publisher publisher;

    private BookInstance bi1;

    private BookInstance bi2;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DomainRepository<Work> workRepository;

    @Autowired
    private DomainRepository<BookInstance> bookInstanceRepository;

    @Autowired
    private DomainRepository<Publisher> publisherRepository;

    @Autowired
    private DomainRepository<Book> bookRepository;

    @Test(expected = NullPointerException.class)
    public void save_ThrowsNullPointerExceptionsAsBookHasNullPublisher() {
        // imaginary work
        Work work = workRepository.findById(5);
        // when saving book, NullPointerException is thrown to test
        // transaction management works and it then rolls back
        this.book = new Book(bookRepository.nextId(), work, null, 1, new DomainArrayList<>());
        initBook(this.book);
        bookRepository.save(book);
    }

    @Test
    public void save_SuccessfullySavesBook() {
        // ImaginaryBook
        Work work = workRepository.findById(5);
        long id = bookRepository.nextId();
        this.book = new Book(id, work, publisher, 1, new DomainArrayList<>());
        initBook(this.book);
        bookRepository.save(this.book);

        Book tBook  = bookRepository.findById(id);
        Publisher tPublisher = publisherRepository.findById(4);
        long tInstanceId1 = tBook.getBookInstances().get(0).getId();
        long tInstanceId2 = tBook.getBookInstances().get(1).getId();
        BookInstance tInstance1 = bookInstanceRepository.findById(tInstanceId1);
        BookInstance tInstance2 = bookInstanceRepository.findById(tInstanceId2);

        assertNotNull(tBook);
        assertNotNull(tPublisher);
        assertNotNull(tInstance1);
        assertNotNull(tInstance2);

        // check if data really have been saved into database
        // and corresponding records exist in database
        assertTrue(recordExists("BookInstance", tInstanceId1));
        assertTrue(recordExists("BookInstance", tInstanceId2));
        assertTrue(recordExists("Publisher", tPublisher.getId()));
        assertTrue(recordExists("Book", id));
    }

    @Test
    public void remove_RemovesBookAndAllAssociatedBookInstances() {
        Work work = workRepository.findById(5);
        long id = bookRepository.nextId();
        book = new Book(id, work, this.publisher, 2, new DomainArrayList<>());
        initBook(book);
        bookRepository.save(book);
        long tInstanceId1 = book.getBookInstances().get(0).getId();
        long tInstanceId2 = book.getBookInstances().get(1).getId();
        bookRepository.remove(book);

        assertFalse(bookRepository.exists(book));
        assertFalse(bookInstanceRepository.exists(book.getBookInstances().get(0)));
        assertFalse(bookInstanceRepository.exists(book.getBookInstances().get(1)));

        // assert that all related records have been deleted from tables
        assertFalse(recordExists("Book", book.getId()));
        assertFalse(recordExists("BookInstance", tInstanceId1));
        assertFalse(recordExists("BookInstance", tInstanceId2));
    }


    private void initPublisher() {
        this.publisher = new Publisher(4, "Imaginary publisher", new DomainArrayList<>());
    }

    private void initBook(Book book) {
        FileInstance file1 = new StandardFileInstance("src/main/resources/file1.txt");
        FileInstance file2 = new StandardFileInstance("src/main/resources/file2.pdf");
        bi1 = new BookInstance(bookInstanceRepository.nextId(),
                book, Isbn13.of("978-90-1235-321-1"), BookFormat.TXT, file1);
        bi2 = new BookInstance(bookInstanceRepository.nextId(),
                book, Isbn13.of("978-91-1235-321-1"), BookFormat.PDF, file2);
        book.addBookInstance(bi1);
        book.addBookInstance(bi2);
    }

    /*
     * Check if any record in table with specified id exists
     */
    private boolean recordExists(String table, long id) {
        String QUERY = "SELECT COUNT(id) FROM " + table + " WHERE id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(QUERY, id);
        rs.next();
        return rs.getLong(1) == 1;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initPublisher();
    }
}
