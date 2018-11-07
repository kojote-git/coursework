package com.jkojote.library.clauses.mysql;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Escaping {

    private Map<String, String> sqlTokens;

    private Pattern sqlTokenPattern;

    private static final String[][] REGEX_REPLACEMENT = {
     //   search       search       sql replacement
     //   string       regex        regex
        {  "'"     ,    "'"    ,    "\\\\'"   },
        {  "\""    ,    "\""   ,    "\\\\\""  },
        {  "\\"    ,    "\\\\" ,    "\\\\\\\\"},
        {  "\t"    ,    "\\t"  ,    "\\\\t"   },
        {  "\n"    ,    "\\n"  ,    "\\\\n"   },
        {  "\b"    ,    "\\x08",    "\\\\b"   },
        {  "\r"    ,    "\\r"  ,    "\\\\r"   },
        {  "\u0000",    "\\x00",    "\\\\0"   },
        {  "\u001A",    "\\x1A",    "\\\\Z"   }
    };

    public Escaping() {
        sqlTokens = new HashMap<>();
        StringBuilder sb = new StringBuilder("(");
        for (String[] srr: REGEX_REPLACEMENT) {
            sqlTokens.put(srr[0], srr[2]);
            if (sb.length() == 1)
                sb.append(srr[1]);
            else
                sb.append("|").append(srr[1]);
        }
        sb.append(")");
        sqlTokenPattern = Pattern.compile(sb.toString());
    }

    public String escape(String input) {
        Matcher matcher = sqlTokenPattern.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, sqlTokens.get(matcher.group(1)));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
