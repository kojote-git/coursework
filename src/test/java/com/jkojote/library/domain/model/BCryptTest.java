package com.jkojote.library.domain.model;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.Assert.assertTrue;

public class BCryptTest {

    @Test
    public void test() {
        String originalPassword = "password";
        String salt = BCrypt.gensalt(12);
        String hashed = BCrypt.hashpw(originalPassword, salt);
        assertTrue(BCrypt.checkpw(originalPassword, hashed));
    }
}
