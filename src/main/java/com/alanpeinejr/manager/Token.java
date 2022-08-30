package com.alanpeinejr.manager;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Token {

    private final String username;
    private final List<Role> roles;
    private final Instant expiration;


    public Token(String username, List<Role> roles, Instant expiration) {
        this.username = username;
        this.roles = roles;
        this.expiration = expiration;
    }

    public String getUsername() {
        return username;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Instant getExpiration() {
        return expiration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(username, token.username) && Objects.equals(roles, token.roles) && Objects.equals(expiration, token.expiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, roles, expiration);
    }
}
