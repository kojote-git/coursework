package com.jkojote.library.domain.model.book.instance.isbn;

import java.util.regex.Pattern;

@Deprecated
public class StandardIsbn13FormatValidator implements Isbn13FormatValidator {

    private final String REGEX = "^97[89]-(0|([1-9][0-9]{0,4}))-([0-9]{1,5})-([0-9]{1,5})-[0-9]$";

    private final Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * @param number
     * @throws InvalidIsbn13Exception if {@code number} is null; number's length is not 17;
     *                                it doesn't matches with regex
     */
    @Override
    public void requireValid(String number) {
        if (number == null)
            InvalidIsbn13Exception.withMessage(number, "number cannot be null");
        if (number.length() != 17)
            InvalidIsbn13Exception.withMessage(number, "isbn number must contain only 13 digits and 4 dashes");
        if (!PATTERN.matcher(number).matches())
            InvalidIsbn13Exception.withMessage(number, "number doesn't represent isbn number");
    }

    @Override
    public boolean validate(String number) {
        if (number == null)
            return false;
        if (number.length() != 17)
            return false;
        return PATTERN.matcher(number).matches();
    }
}
