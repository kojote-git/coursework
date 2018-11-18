package com.jkojote.library.domain.model.book.instance.isbn;

import java.util.regex.Pattern;

public class SimpleIsbn13FormatValidator implements Isbn13FormatValidator {

    private final static String REGEX2 =
        "(^97[89]-[0-9]{10}$)";

    private static final Pattern PATTERN = Pattern.compile(REGEX2);
    @Override
    public void requireValid(String number) {
        if (number == null)
            InvalidIsbn13Exception.withMessage(number, "number cannot be null");
        if (!PATTERN.matcher(number).matches())
            InvalidIsbn13Exception.invalidNumber(number);
    }

    @Override
    public boolean validate(String number) {
        if (number == null)
            return false;
        return PATTERN.matcher(number).matches();
    }
}
