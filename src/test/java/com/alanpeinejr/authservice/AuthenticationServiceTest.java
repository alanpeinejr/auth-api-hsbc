package com.alanpeinejr.authservice;

import com.alanpeinejr.encryption.EncryptionManager;
import com.alanpeinejr.exception.*;
import com.alanpeinejr.manager.*;
import com.google.gson.JsonSyntaxException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Full integration test for the API
 */
public class AuthenticationServiceTest {

    private final static int TOKEN_LIFETIME_IN_SECONDS = 5;
    private AuthenticationService authenticationService;
    private HashMap<String, User> userStorage;
    private HashMap<String, Role> roleStorage;

    @Before()
    public void before() {
        userStorage = new HashMap<>();
        roleStorage = new HashMap<>();
        authenticationService =
                new AuthenticationServiceBuilder()
                        .createAuthenticationService()
                        .setupUserStorage(userStorage)
                        .setupRoleStorage(roleStorage)
                        .userManager(new UserManager(userStorage))
                        .roleManager(new RoleManager(roleStorage))
                        .tokenManager(new TokenManager(TOKEN_LIFETIME_IN_SECONDS))
                        .encryptionManager(new EncryptionManager())
                        .build();
    }

    @Test
    public void testCreateUserValid() throws DuplicateUserException {
        authenticationService.createUser("user", "password");

        assertEquals(1, userStorage.size());
        assertNotNull(userStorage.get("user"));
    }

    @Test
    public void testCreateUserError() throws DuplicateUserException {
        authenticationService.createUser("user", "password1");

        assertThrows(DuplicateUserException.class, () -> authenticationService.createUser("user", "password2"));
        assertEquals(1, userStorage.size());
        assertNotNull(userStorage.get("user"));
    }

    @Test
    public void testDeleteUserValid() throws NoSuchUserException, DuplicateUserException {
        User user = authenticationService.createUser("user", "password1");
        authenticationService.deleteUser(user);
        assertEquals(0, userStorage.size());
    }

    @Test
    public void testDeleteUserError() throws NoSuchUserException, DuplicateUserException {
        User user = authenticationService.createUser("user", "password1");
        authenticationService.deleteUser(user);

        assertThrows(NoSuchUserException.class, () -> authenticationService.deleteUser(user));
    }

    @Test
    public void testCreateRoleValid() throws DuplicateRoleException {
        authenticationService.createRole("role");

        assertEquals(1, roleStorage.size());
        assertNotNull(roleStorage.get("role"));
    }

    @Test
    public void testCreateRoleError() throws DuplicateRoleException {
        authenticationService.createRole("role");

        assertThrows(DuplicateRoleException.class, () -> authenticationService.createRole("role"));
        assertEquals(1, roleStorage.size());
        assertNotNull(roleStorage.get("role"));
    }

    @Test
    public void testDeleteRoleValid() throws NoSuchRoleException, DuplicateRoleException {
        Role role = authenticationService.createRole("role");
        authenticationService.deleteRole(role);
        assertEquals(0, roleStorage.size());
    }


    @Test
    public void testDeleteRoleError() throws NoSuchRoleException, DuplicateRoleException {
        Role role = authenticationService.createRole("role");
        authenticationService.deleteRole(role);

        assertThrows(NoSuchRoleException.class, () -> authenticationService.deleteRole(role));
    }

    @Test
    public void testAssignRoleToUserError() throws DuplicateRoleException {
        User user = new User("user", "password", 0);
        Role role = authenticationService.createRole("role");

        assertThrows(NoSuchUserException.class, () -> authenticationService.assignRoleToUser(user, role));
    }

    @Test
    public void testAuthenticateValid() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException {
        authenticationService.createUser("user", "password");
        authenticationService.createRole("role");

        String token = authenticationService.authenticate("user", "password");
        assertNotEquals("Invalid Password", token);
        assertNotEquals("", token);
        assertNotNull(token);

    }

    @Test
    public void testAuthenticateInvalidPassword() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException {
        authenticationService.createUser("user", "password");
        authenticationService.createRole("role");

        String token = authenticationService.authenticate("user", "wrong");
        assertEquals("Invalid Password", token);

    }

    @Test
    public void testAuthenticateError() {
        assertThrows(NoSuchUserException.class, () -> authenticationService.authenticate("user", "password"));

    }

    @Test
    public void testInvalidateError() {
        assertThrows(JsonSyntaxException.class, () -> authenticationService.invalidate("aW52YWxpZCB0b2tlbg=="));
    }

    @Test
    public void testAuthorizeValid() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException, ExpiredTokenException {
        User user = authenticationService.createUser("user", "password");
        Role role = authenticationService.createRole("role");
        authenticationService.assignRoleToUser(user, role);
        String token = authenticationService.authenticate("user", "password");

        sleep(10);

        assertTrue(authenticationService.authorize(token, role));

    }

    @Test
    public void testAuthorizeNoSuchRole() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException, ExpiredTokenException {
        authenticationService.createUser("user", "password");
        Role role = authenticationService.createRole("role");
        String token = authenticationService.authenticate("user", "password");

        sleep(10);

        assertFalse(authenticationService.authorize(token, role));

    }

    @Test
    public void testAuthorizeNoUser() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException {
        User user = authenticationService.createUser("user", "password");
        Role role = authenticationService.createRole("role");
        String token = authenticationService.authenticate("user", "password");
        authenticationService.deleteUser(user);

        sleep(10);

        assertThrows(ExpiredTokenException.class, () -> authenticationService.authorize(token, role));

    }

    @Test
    public void testAuthorizeOldToken() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException {
        authenticationService.createUser("user", "password");
        Role role = authenticationService.createRole("role");
        String token = authenticationService.authenticate("user", "password");

        //longer than 5 seconds
        sleep(5001);

        assertThrows(ExpiredTokenException.class, () -> authenticationService.authorize(token, role));
    }

    @Test
    public void testAuthorizeInvalidatedToken() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException {
        authenticationService.createUser("user", "password");
        Role role = authenticationService.createRole("role");
        String token = authenticationService.authenticate("user", "password");
        authenticationService.invalidate(token);

        sleep(10);

        assertThrows(ExpiredTokenException.class, () -> authenticationService.authorize(token, role));
    }

    @Test
    public void testGetAllRolesValid() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException, ExpiredTokenException {
        User user = authenticationService.createUser("user", "password");
        Role role1 = authenticationService.createRole("role1");
        Role role2 = authenticationService.createRole("role2");
        authenticationService.assignRoleToUser(user, role1);
        authenticationService.assignRoleToUser(user, role2);

        String token = authenticationService.authenticate("user", "password");

        sleep(10);
        Role[] expected = new Role[]{role1, role2};
        Role[] actual = authenticationService.getAllRoles(token);
        assertEquals(expected.length, actual.length);
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);

    }

    @Test
    public void testGetAllRolesNoUser() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException {
        User user = authenticationService.createUser("user", "password");
        authenticationService.createRole("role");
        String token = authenticationService.authenticate("user", "password");
        authenticationService.deleteUser(user);

        sleep(10);

        assertThrows(ExpiredTokenException.class, () -> authenticationService.getAllRoles(token));

    }

    @Test
    public void testGetAllRolesOldToken() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException {
        authenticationService.createUser("user", "password");
        authenticationService.createRole("role");
        String token = authenticationService.authenticate("user", "password");

        //longer than 5 seconds
        sleep(5001);

        assertThrows(ExpiredTokenException.class, () -> authenticationService.getAllRoles(token));
    }

    @Test
    public void testGetAllRolesInvalidatedToken() throws DuplicateRoleException, NoSuchUserException, DuplicateUserException {
        authenticationService.createUser("user", "password");
        authenticationService.createRole("role");
        String token = authenticationService.authenticate("user", "password");
        authenticationService.invalidate(token);

        sleep(10);

        assertThrows(ExpiredTokenException.class, () -> authenticationService.getAllRoles(token));
    }

    @Test
    public void testDeleteRolesAreDeletedForUsers() throws NoSuchRoleException, DuplicateRoleException, DuplicateUserException, NoSuchUserException, ExpiredTokenException {
        User user = authenticationService.createUser("user", "password");
        Role role = authenticationService.createRole("role");
        authenticationService.assignRoleToUser(user, role);
        authenticationService.deleteRole(role);

        String token = authenticationService.authenticate("user", "password");

        sleep(10);

        assertFalse(authenticationService.authorize(token, role));
        assertEquals(0, roleStorage.size());
    }

    private void sleep(long timeMilliS) {
        try {
            Thread.sleep(timeMilliS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
