package com.alanpeinejr.manager;

import com.alanpeinejr.encryption.IDecrypt;
import com.alanpeinejr.encryption.IEncrypt;
import com.google.gson.JsonSyntaxException;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TokenManagerTest {

    private final static String ENCRYPTED = "1";
    private final static int TOKEN_LIFE = 5;

    private TokenManager tokenManager;
    private TokenManagerTest.MockEncryptor mockEncryptor;
    private TokenManagerTest.MockDecryptor mockDecryptor;
    private TokenManagerTest.MockUserManager mockUserManager;

    @Before()
    public void before() {
        mockEncryptor = new TokenManagerTest.MockEncryptor();
        mockDecryptor = new TokenManagerTest.MockDecryptor();
        mockUserManager = new MockUserManager();
        tokenManager = new TokenManager(TOKEN_LIFE);

    }

    @Test
    public void testGetTokenString() {
        User user = new User("testUser", "testPassword", 0);
        mockUserManager.addRole(user, new Role("testRole1"));
        mockUserManager.authenticate(user);

        assertEquals("{\"username\":\"testUser\",\"roles\":[{\"name\":\"testRole1\"}],\"expiration\":" + user.getLoginTime().plusSeconds(TOKEN_LIFE).getEpochSecond() + "}1", tokenManager.getTokenString(user, mockEncryptor));
    }

    @Test
    public void testReadValidToken() {
        User user = new User("testUser", "testPassword", 0);
        mockUserManager.addRole(user, new Role("testRole1"));
        mockUserManager.authenticate(user);
        String encryptedToken = "{\"username\":\"testUser\",\"roles\":[{\"name\":\"testRole1\"}],\"expiration\":" + user.getLoginTime().plusSeconds(TOKEN_LIFE).getEpochSecond() + "}1";
        assertEquals(new Token("testUser", user.getRoles(), user.getLoginTime().plusSeconds(TOKEN_LIFE)), tokenManager.readTokenString(encryptedToken, mockDecryptor));
    }

    @Test
    public void testReadInvalidToken() {
        assertThrows(JsonSyntaxException.class, () -> tokenManager.readTokenString("invalidToken", mockDecryptor));
    }

    private static class MockEncryptor implements IEncrypt {
        @Override
        public String encrypt(String string) {
            return string + ENCRYPTED;
        }
    }

    private static class MockDecryptor implements IDecrypt {
        @Override
        public String decrypt(String string) {
            return string.substring(0, string.length() - 1);
        }
    }

    private static class MockUserManager {
        public void authenticate(User user) {
            user.setLoginTime(Instant.EPOCH);
        }

        public void addRole(User user, Role role) {
            user.getRoles().add(role);
        }
    }
}
