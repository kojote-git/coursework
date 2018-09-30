package com.jkojote.library.domain.model.book.instance.isbn;

public class InvalidIsbn13Exception extends RuntimeException {

    private String invalidValue;

    private InvalidIsbn13Exception(String number, String message) {
        super(message);
    }

    public static InvalidIsbn13Exception invalidNumber(String number) {
        throw new InvalidIsbn13Exception(number, "invalid number: " + number);
    }

    public static InvalidIsbn13Exception withMessage(String number, String message) {
        throw new InvalidIsbn13Exception(number, message);
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}
