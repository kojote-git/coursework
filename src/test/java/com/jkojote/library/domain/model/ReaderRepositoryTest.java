package com.jkojote.library.domain.model;

import com.jkojote.library.clauses.SqlClause;
import com.jkojote.library.clauses.mysql.MySqlClauseBuilder;
import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.reader.Download;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.domain.shared.domain.FilteringAndSortingRepository;
import com.jkojote.library.persistence.TableProcessor;
import com.jkojote.types.Email;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.jkojote.library.domain.model.reader.Reader.ReaderBuilder;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class ReaderRepositoryTest {

    @Autowired
    private FilteringAndSortingRepository<Reader> readerRepository;

    @Autowired
    private DomainRepository<BookInstance> bookInstanceRepository;

    @Autowired
    private TableProcessor<Download> downloadTable;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void findById() {
        Reader reader = readerRepository.findById(1);
        assertTrue(reader.hasSamePassword("password"));
        assertEquals(Email.of("reader@mail.com"), reader.getEmail());
    }

    @Test
    public void findAll() {
        MySqlClauseBuilder select = new MySqlClauseBuilder();
        SqlClause clause = select.where("email").like("reader@mail.com")
                .build();
        List<Reader> readers = readerRepository.findAll(clause);
        assertEquals(1, readers.size());
    }

    @Test
    @DirtiesContext
    public void save() {
        long id = readerRepository.nextId();
        Reader reader = ReaderBuilder.aReader()
                .withId(id)
                .withEmail(Email.of("reader1@mail.com"))
                .withPassword("password")
                .build();
        BookInstance bi1 = bookInstanceRepository.findById(1);
        BookInstance bi2 = bookInstanceRepository.findById(2);
        reader.addToDownloadHistory(bi1, 10);
        reader.addToDownloadHistory(bi2, 10);
        readerRepository.save(reader);
        for (Download d : reader.getDownloads())
            assertTrue(downloadTable.exists(d));
    }

    @Test
    @DirtiesContext
    public void remove() {
        long id = readerRepository.nextId();
        Reader reader = ReaderBuilder.aReader()
                .withId(id)
                .withEmail(Email.of("reader1@mail.com"))
                .withPassword("password")
                .build();
        BookInstance bi1 = bookInstanceRepository.findById(1);
        BookInstance bi2 = bookInstanceRepository.findById(2);
        reader.addToDownloadHistory(bi1, 10);
        reader.addToDownloadHistory(bi2, 10);
        readerRepository.save(reader);
        readerRepository.remove(readerRepository.findById(id));
        assertFalse(readerRepository.exists(reader));
        for (Download d : reader.getDownloads())
            assertFalse(downloadTable.exists(d));
    }


}
