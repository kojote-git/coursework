package com.jkojote.library.domain.model;

import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.reader.Download;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.types.Email;
import org.junit.Before;
import org.junit.Test;

import static com.jkojote.library.domain.model.reader.Reader.ReaderBuilder;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReaderTest {

    private Reader reader;

    private BookInstance bi1;

    private BookInstance bi2;

    @Before
    public void init() {
        this.reader = ReaderBuilder.aReader()
                .withId(1)
                .withEmail(Email.of("reader@reader.com"))
                .withPassword("readerPassword")
                .build();
        this.bi1 = mock(BookInstance.class);
        this.bi2 = mock(BookInstance.class);
        when(bi1.getId()).thenReturn(1L);
        when(bi2.getId()).thenReturn(2L);
    }

    @Test
    public void hasSamePassword() {
        assertTrue(reader.hasSamePassword("readerPassword"));
        assertFalse(reader.hasSamePassword("readerPassword1"));
    }

    @Test
    public void changePassword() {
        String newPassword = "readerPassword1";
        String oldPassword = "readerPassword";
        reader.changePassword(oldPassword, newPassword);
        assertFalse(reader.hasSamePassword(oldPassword));
        assertTrue(reader.hasSamePassword(newPassword));
    }

    @Test
    public void addToDownloadHistory() {
        assertFalse(reader.addToDownloadHistory(null));
        // download history doesn't contain these two instances
        assertTrue(reader.addToDownloadHistory(bi1));
        assertTrue(reader.addToDownloadHistory(bi2, 5));
        // now it has
        assertFalse(reader.addToDownloadHistory(bi1));
        assertFalse(reader.addToDownloadHistory(bi2, 5));
    }

    @Test
    public void removeFromDeaderHistory() {
        // download history hasn't records associated with these instance
        assertFalse(reader.removeFromDownloadHistory(bi1));
        assertFalse(reader.removeFromDownloadHistory(bi2));
        reader.addToDownloadHistory(bi1);
        reader.addToDownloadHistory(bi2);
        // now it has
        assertTrue(reader.removeFromDownloadHistory(bi1));
        assertTrue(reader.removeFromDownloadHistory(bi2));
    }

    @Test
    public void updateRating() {
        reader.addToDownloadHistory(bi2);
        Download d = reader.getDownloads().get(0);
        assertEquals(0, d.getReaderRating());
        assertFalse(reader.updateRating(bi1, 10));
        assertTrue(reader.updateRating(bi2, 10));
        d = reader.getDownloads().get(0);
        assertEquals(10, d.getReaderRating());
    }
}
