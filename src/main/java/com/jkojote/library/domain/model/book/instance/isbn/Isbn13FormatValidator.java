package com.jkojote.library.domain.model.book.instance.isbn;

/**
 * Rules of validation may be not so strict
 */
public interface Isbn13FormatValidator {

    /**
     * Checks whether number is valid isbn number and throws {@link InvalidIsbn13Exception} if it's not
     * @param number
     * @throws InvalidIsbn13Exception if {@code number} is invalid
     */
    void requireValid(String number);

    /**
     * @param number
     * @return {@code true} if number is valid
     */
    boolean validate(String number);
}
