package com.alanpeinejr.manager;

import com.alanpeinejr.encryption.IEncrypt;
import com.alanpeinejr.exception.DuplicateUserException;
import com.alanpeinejr.exception.NoSuchUserException;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;


public class UserManagerTest {

    private static final String ENCRYPTED = "encrypted";
    private HashMap<String, User> userHashMap;
    private UserManager userManager;
    private MockEncryptor mockEncryptor;

    @Before()
    public void before() {
        userHashMap = new HashMap<>();
        userManager = new UserManager(userHashMap);
        mockEncryptor = new MockEncryptor();
    }

    @Test
    public void testCreateUserFirstUser() throws DuplicateUserException {
        userManager.createUser("testUser", "testPassword", mockEncryptor);
        assertTrue(userHashMap.containsKey("testUser"));
        assertEquals("testUser", userHashMap.get("testUser").getUsername());
        assertEquals(1, userHashMap.size());
    }

    @Test
    public void testCreateUserPasswordIsEncrypted() throws DuplicateUserException {
        userManager.createUser("testUser", "testPassword", mockEncryptor);
        assertNotEquals("testPassword", userHashMap.get("testUser").getPassword());
    }

    @Test
    public void testCreateUserNoDuplicateUsers() throws DuplicateUserException {
        userManager.createUser("testUser", "testPassword", mockEncryptor);
        assertThrows(DuplicateUserException.class, () -> userManager.createUser("testUser", "testPassword", mockEncryptor));
    }

    @Test
    public void testCreateUserExposedEncryptionProtection() throws DuplicateUserException {
        userManager.createUser("testUser", "testPassword", mockEncryptor);
        assertNotEquals("testPassword" + ENCRYPTED, userHashMap.get("testUser").getPassword());
    }

    @Test
    public void testDeleteUserValidDelete() throws NoSuchUserException {
        userHashMap.put("testUser", new User("testUser", "testPassword", 0));
        userManager.deleteUser("testUser");
        assertEquals(0, userHashMap.size());
    }

    @Test
    public void testDeleteUserUserDoesntExist() {
        assertThrows(NoSuchUserException.class, () -> userManager.deleteUser("testUser"));
    }

    @Test
    public void testAddRoleToUserUserDoesntHaveRole() throws NoSuchUserException {
        User user = new User("testUser", "testPassword", 0);
        userHashMap.put("testUser", user);

        userManager.addRoleToUser(user, new Role("newRole"));

        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(new Role("newRole")));
    }

    @Test
    public void testAddRoleToUserUserDoesHaveRole() throws NoSuchUserException {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new Role("newRole"));
        User user = new User("testUser", "testPassword", 0, roles);
        userHashMap.put("testUser", user);

        userManager.addRoleToUser(user, new Role("newRole"));

        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(new Role("newRole")));
    }

    @Test
    public void testAddRoleToUserUserDoesntExist() {
        User user = new User("testUser", "testPassword", 0);

        assertThrows(NoSuchUserException.class, () -> userManager.addRoleToUser(user, new Role("newRole")));
    }

    @Test
    public void testAuthenticateCorrectPassword() throws NoSuchUserException {
        double salt = 0;
        userHashMap.put("testUser", new User("testUser", "testPassword" + salt + ENCRYPTED, salt));
        assertTrue(userManager.authenticate("testUser", "testPassword", mockEncryptor));
    }

    @Test
    public void testAuthenticateIncorrectPassword() throws NoSuchUserException {
        double salt = 0;
        userHashMap.put("testUser", new User("testUser", "testPassword" + salt + ENCRYPTED, salt));
        assertFalse(userManager.authenticate("testUser", "wrongPassword", mockEncryptor));
    }

    @Test
    public void testAuthenticateUserDoesntExist() {
        assertThrows(NoSuchUserException.class, () -> userManager.authenticate("testUser", "wrongPassword", mockEncryptor));
    }

    @Test
    public void testAuthenticateRecordsLogin() throws NoSuchUserException {
        double salt = 0;
        userHashMap.put("testUser", new User("testUser", "testPassword" + salt + ENCRYPTED, salt));
        assertTrue(userManager.authenticate("testUser", "testPassword", mockEncryptor));
        assertNotNull(userHashMap.get("testUser").getLoginTime());
    }

    @Test
    public void testAuthenticateRefreshLogin() throws NoSuchUserException {
        double salt = 0;
        userHashMap.put("testUser", new User("testUser", "testPassword" + salt + ENCRYPTED, salt));

        assertTrue(userManager.authenticate("testUser", "testPassword", mockEncryptor));
        assertNotNull(userHashMap.get("testUser").getLoginTime());

        Instant firstLogin = userHashMap.get("testUser").getLoginTime();

        try {
            //added because my test was able to run fast enough to ge the same time...
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //2nd login
        assertTrue(userManager.authenticate("testUser", "testPassword", mockEncryptor));
        assertTrue(firstLogin.isBefore(userHashMap.get("testUser").getLoginTime()));

    }

    @Test
    public void testRemoveRoleFromAllUsers() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new Role("newRole"));
        roles.add(new Role("role2"));
        User user = new User("testUser1", "testPassword", 0, roles);
        User user2 = new User("testUser2", "testPassword", 0, roles);
        userHashMap.put("testUser1", user);
        userHashMap.put("testUser2", user2);

        userManager.removeRoleFromAllUsers(new Role("role2"));
        assertEquals(1, user.getRoles().size());
        assertEquals(1, user2.getRoles().size());

    }

    @Test
    public void testInvalidate() {
        ArrayList<Role> roles = new ArrayList<>();
        User user = new User("testUser1", "testPassword", 0, roles);
        user.setLoginTime(Instant.EPOCH);
        userHashMap.put("testUser1", user);

        userManager.invalidate("testUser1");
        assertNull(user.getLoginTime());

    }


    private static class MockEncryptor implements IEncrypt {
        @Override
        public String encrypt(String string) {
            return string + ENCRYPTED;
        }
    }
}
