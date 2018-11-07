package com.jkojote.library.domain.model;

import com.jkojote.library.clauses.mysql.Escaping;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EscapingTest {

    private Escaping escaping = new Escaping();

    @Test
    public void appendReplacement() {
        String doubleQuotes = "She said, \"Where can we find a nice Indian restaurant?\"";
        String singleQuotes  = "It's been raining for a while";
        String singleQuotes1 = "It's been raining for a while and it's cold";
        String quotes  = "She said, \"It's been raining for a while and it's cold\"";
        String quotes1  = "She said, 'It's been raining for a while and it's cold'";

        String escapedDoubleQuotes = escaping.escape(doubleQuotes);
        String escapedSingleQuotes = escaping.escape(singleQuotes);
        String escapedSingleQuotes1 = escaping.escape(singleQuotes1);
        String escapedQuotes = escaping.escape(quotes);
        String escapedQuotes1 = escaping.escape(quotes1);

        String expectedDoubleQuotes =
            "She said, \\\"Where can we find a nice Indian restaurant?\\\"";
        String expectedSingleQuotes =
            "It\\'s been raining for a while";
        String expectedSingleQuotes1 =
            "It\\'s been raining for a while and it\\'s cold";
        String expectedQuotes =
            "She said, \\\"It\\'s been raining for a while and it\\'s cold\\\"";
        String expectedQuotes1 =
            "She said, \\'It\\'s been raining for a while and it\\'s cold\\'";

        assertEquals(expectedDoubleQuotes, escapedDoubleQuotes);
        assertEquals(expectedSingleQuotes, escapedSingleQuotes);
        assertEquals(expectedSingleQuotes1, escapedSingleQuotes1);
        assertEquals(expectedQuotes, escapedQuotes);
        assertEquals(expectedQuotes1, escapedQuotes1);
    }
}
