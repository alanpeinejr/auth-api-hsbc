package com.alanpeinejr.authservice;

import com.alanpeinejr.exception.DuplicateRoleException;
import com.alanpeinejr.exception.DuplicateUserException;
import com.alanpeinejr.exception.ExpiredTokenException;
import com.alanpeinejr.exception.NoSuchRoleException;
import com.alanpeinejr.exception.NoSuchUserException;
import com.alanpeinejr.manager.Role;
import com.alanpeinejr.manager.Token;
import com.alanpeinejr.manager.User;
import com.google.gson.JsonSyntaxException;

import java.time.Clock;

public class AuthenticationService extends AbstractAuthenticationService {


    @Override
    User createUser(String username, String password) throws DuplicateUserException {
        return this.getUserManager().createUser(username, password, this.getEncryptionManager());
    }

    @Override
    void deleteUser(User user) throws NoSuchUserException {
        this.getUserManager().deleteUser(user.getUsername());
    }

    @Override
    Role createRole(String roleName) throws DuplicateRoleException {
        return this.getRoleManager().createRole(roleName);
    }

    @Override
    void deleteRole(Role role) throws NoSuchRoleException {
        this.getRoleManager().deleteRole(role.getName());
        this.getUserManager().removeRoleFromAllUsers(role);
    }

    @Override
    void assignRoleToUser(User user, Role role) throws NoSuchUserException {
        this.getUserManager().addRoleToUser(user, role);
    }

    @Override
    String authenticate(String username, String password) throws NoSuchUserException {
        if (this.getUserManager().authenticate(username, password, this.getEncryptionManager())) {
            return this.getTokenManager().getTokenString(getUserStorage().get(username), this.getEncryptionManager());
        }
        return "Invalid Password";
    }

    @Override
    void invalidate(String tokenString) throws JsonSyntaxException {
        Token token = this.getTokenManager().readTokenString(tokenString, this.getEncryptionManager());
        this.getUserManager().invalidate(token.getUsername());
    }

    @Override
    boolean authorize(String tokenString, Role role) throws ExpiredTokenException, JsonSyntaxException {
        return validate(tokenString).getRoles().contains(role);
    }

    @Override
    Role[] getAllRoles(String token) throws ExpiredTokenException, JsonSyntaxException {
        return validate(token).getRoles().toArray(new Role[0]);
    }

    /**
     * Helper to handle validating token
     */
    private Token validate(String tokenString) throws ExpiredTokenException, JsonSyntaxException {
        Token token = this.getTokenManager().readTokenString(tokenString, this.getEncryptionManager());
        User user = getUserStorage().get(token.getUsername());
        if (token.getExpiration().isBefore(Clock.systemUTC().instant()) || user == null || user.getLoginTime() == null) {
            throw new ExpiredTokenException("Token has expired");
        }
        return token;
    }
}
