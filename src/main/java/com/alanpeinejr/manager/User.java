package com.alanpeinejr.manager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Main API for the User
 *
 * When a user is created, a salt is added to the password and then "encrypted". This keeps information local to this server
 * that will never be sent over the wire, and that helps protect a brute force attack or leaked password storage. When a user is authenticated,
 * the salt will need to be added to the provided password in order to compare against the stored password.
 */
public class User {

    private final double salt;
    private String username;
    private String password;
    private List<Role> roles;
    private Instant loginTime;

    public User(String username, String esPassword, double salt, List<Role> roles) {
        this.username = username;
        this.password = esPassword;
        this.roles = roles;
        this.salt = salt;
    }

    public User(String username, String esPassword, double salt) {
        this(username, esPassword, salt, new ArrayList<>());
    }


    public String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected double getSalt() {
        return salt;
    }

    protected List<Role> getRoles() {
        return roles;
    }

    protected void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Instant getLoginTime() {
        return loginTime;
    }

    protected void setLoginTime(Instant loginTime) {
        this.loginTime = loginTime;
    }
}
