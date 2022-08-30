package com.alanpeinejr.authservice;

import com.alanpeinejr.encryption.EncryptionManager;

import com.alanpeinejr.exception.DuplicateRoleException;
import com.alanpeinejr.exception.DuplicateUserException;
import com.alanpeinejr.exception.ExpiredTokenException;
import com.alanpeinejr.exception.NoSuchRoleException;
import com.alanpeinejr.exception.NoSuchUserException;
import com.alanpeinejr.manager.Role;
import com.alanpeinejr.manager.RoleManager;
import com.alanpeinejr.manager.TokenManager;
import com.alanpeinejr.manager.User;
import com.alanpeinejr.manager.UserManager;
import com.google.gson.JsonSyntaxException;

import java.util.Map;

/**
 * Abstract Class to Define all functions the API completes
 * <p>
 * Implements IAuthenticationService for ease of building
 */
abstract class AbstractAuthenticationService implements IAuthenticationService {
    private Map<String, User> userStorage;
    private Map<String, Role> roleStorage;
    private UserManager userManager;
    private RoleManager roleManager;
    private TokenManager tokenManager;
    private EncryptionManager encryptionManager;

    /**
     * Creates a new user
     *
     * @param username the unique name of a user
     * @param password the desired password for user
     * @return User created by the system
     * @throws DuplicateUserException If the username is taken
     */
    abstract User createUser(String username, String password) throws DuplicateUserException;

    /**
     * Deletes the user
     *
     * @param user User intended to be deleted
     * @throws NoSuchUserException If the user does not exist in system
     */
    abstract void deleteUser(User user) throws NoSuchUserException;

    /**
     * Creates a new role
     *
     * @param roleName the unique name of a role
     * @return Role created by the system
     * @throws DuplicateRoleException If the roleName already exists
     */
    abstract Role createRole(String roleName) throws DuplicateRoleException;

    /**
     * Deletes the Role
     *
     * @param role Role intended to be deleted, will remove Role from all Users that have it
     * @throws NoSuchRoleException If the role does not exist in the system
     */
    abstract void deleteRole(Role role) throws NoSuchRoleException;

    /**
     * Assigns the Role to the provided User
     * <p>
     * Provided docs don't list an error for if the role doesn't exist, but I think it should if User does not exist.
     * User could still be live in the system while having been deleted by a different call
     *
     * @param user User that role will be added to
     * @param role Role that will be added to the user
     * @throws NoSuchUserException If the user does not exist
     */
    abstract void assignRoleToUser(User user, Role role) throws NoSuchUserException;

    /**
     * Returns a token if user exists, and their provided password is correct, otherwise throws an error or returns Invalid Password
     * <p>
     * API says it should through an error if User doesnt exist. I implemented it but I think this is wrong. It allows users to be enumerated.
     *
     * @param username unique name of the User attempting to login
     * @param password password of the User attempting to login
     * @return Token representing the user's name, roles, and an expiration time of the token, "Invalid Password" if authentication fails.
     * @throws NoSuchUserException If the user does not exist
     */
    abstract String authenticate(String username, String password) throws NoSuchUserException;

    /**
     * Invalidates the given token
     *
     * @param token Token representing the user's name, roles, and an expiration time of the token
     * @throws JsonSyntaxException If token provided cannot be read
     */
    abstract void invalidate(String token) throws JsonSyntaxException;

    /**
     * Checks if the user represented by the token has the given role
     *
     * @param token Token representing the user's name, roles, and an expiration time of the token
     * @param role  Role being checked for
     * @return True if the user has the given role, otherwise false
     * @throws ExpiredTokenException If the token has been invalidated, or if the time is past
     * @throws JsonSyntaxException   If token provided cannot be read
     */
    abstract boolean authorize(String token, Role role) throws ExpiredTokenException, JsonSyntaxException;

    /**
     * Returns all Roles user had when the token was generated
     *
     * @param token Token representing the user's name, roles, and an expiration time of the token
     * @return An array of all Roles this user has within the token
     * @throws ExpiredTokenException If the token has been invalidated, or if the time is past
     * @throws JsonSyntaxException   If token provided cannot be read
     */
    abstract Role[] getAllRoles(String token) throws ExpiredTokenException, JsonSyntaxException;

    public Map<String, User> getUserStorage() {
        return userStorage;
    }

    @Override
    public void setUserStorage(Map<String, User> userStorage) {
        this.userStorage = userStorage;
    }

    public Map<String, Role> getRoleStorage() {
        return roleStorage;
    }

    @Override
    public void setRoleStorage(Map<String, Role> roleStorage) {
        this.roleStorage = roleStorage;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    @Override
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    @Override
    public void setTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public EncryptionManager getEncryptionManager() {
        return encryptionManager;
    }

    @Override
    public void setEncryptionManager(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }
}
