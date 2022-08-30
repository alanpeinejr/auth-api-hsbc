package com.alanpeinejr.manager;

import com.alanpeinejr.exception.DuplicateRoleException;
import com.alanpeinejr.exception.NoSuchRoleException;

import java.util.Map;

public class RoleManager {
    private final Map<String, Role> roleStorage;

    public RoleManager(Map<String, Role> roleStorage) {
        this.roleStorage = roleStorage;
    }

    public Role createRole(String roleName) throws DuplicateRoleException {
        if (roleStorage.containsKey(roleName)) {
            throw new DuplicateRoleException("Role already exists");
        }
        Role role = new Role(roleName);
        roleStorage.put(roleName, role);
        return role;
    }

    public void deleteRole(String roleName) throws NoSuchRoleException {
        if (!roleStorage.containsKey(roleName)) {
            throw new NoSuchRoleException("No Such Role Exists");
        }
        roleStorage.remove(roleName);
    }
}
