package com.jkojote.library.domain.model;

import com.jkojote.library.domain.model.book.instance.isbn.Isbn13FormatValidator;
import com.jkojote.library.domain.model.book.instance.isbn.SimpleIsbn13FormatValidator;
import com.jkojote.library.domain.model.book.instance.isbn.StandardIsbn13FormatValidator;
import org.junit.Test;

import static org.junit.Assert.*;

public class Isbn13FormatValidatorTest {

//    private Isbn13FormatValidator validator = new StandardIsbn13FormatValidator();
    private Isbn13FormatValidator validator = new SimpleIsbn13FormatValidator();

    @Test
    public void validate_OnlyValidNumbers() {
//        assertTrue(validate("978-0-1523-1221-1"));
//        assertTrue(validate("979-1-1523-1221-2"));
//        assertTrue(validate("978-152-1333-22-1"));
//        assertTrue(validate("978-90-142-1331-1"));
//        assertTrue(validate("978-23-097-2313-1"));
//        assertTrue(validate("979-230-243-123-9"));
//        assertTrue(validate("978-42-975-1640-5"));
//        assertTrue(validate("979-32-42322-21-2"));
        assertTrue(validate("978-3212321249"));
        assertTrue(validate("979-3212321249"));
    }

    @Test
    public void validate_OnlyInvalidNumbers() {
//        assertFalse(validate(null));
//        assertFalse(validate(""));
//        assertFalse(validate("977-0-1523-1221-2"));
//        assertFalse(validate("978-123-3421-3-12"));
//        assertFalse(validate("978-1-1-1-1"));
//        assertFalse(validate("978-00-124-1234-1"));
//        assertFalse(validate("abcdefghijklmnopq"));
        assertFalse(validate(null));
        assertFalse(validate("978-"));
        assertFalse(validate("979"));
        assertFalse(validate(""));
    }

    private boolean validate(String number) {
        return validator.validate(number);
    }
}
