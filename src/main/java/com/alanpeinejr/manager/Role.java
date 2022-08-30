package com.alanpeinejr.manager;

import java.util.Objects;

/**
 * Main API for the Role
 * Role is currently only a name. Other info could be added later, like an ID number
 */
public class Role {
    private final String name;

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
