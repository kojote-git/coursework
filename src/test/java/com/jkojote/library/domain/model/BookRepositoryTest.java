package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class BookRepositoryTest {

    @Autowired
    private DomainRepository<Book> bookRepository;

    @Test
    public void findById() {
        Book book = bookRepository.findById(1);
        assertNotNull(book);
        assertEquals(1, book.getId());
        assertEquals("The God Delusion", book.getBasedOn().getTitle());
    }

    @Test
    public void findAll() {
        List<Book> books = bookRepository.findAll();
        assertNotNull(books);
    }

    @Test
    public void exists() {
        Book book = bookRepository.findById(1);
        assertTrue(bookRepository.exists(book));
    }
}
