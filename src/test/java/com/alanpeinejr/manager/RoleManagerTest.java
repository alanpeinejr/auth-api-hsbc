package com.alanpeinejr.manager;

import com.alanpeinejr.exception.DuplicateRoleException;
import com.alanpeinejr.exception.NoSuchRoleException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class RoleManagerTest {
    private HashMap<String, Role> roleHashMap;
    private RoleManager roleManager;

    @Before()
    public void before() {
        roleHashMap = new HashMap<>();
        roleManager = new RoleManager(roleHashMap);
    }

    @Test
    public void testCreateRoleFirstRole() throws DuplicateRoleException {
        roleManager.createRole("testRole");
        assertTrue(roleHashMap.containsKey("testRole"));
        assertEquals("testRole", roleHashMap.get("testRole").getName());
        assertEquals(1, roleHashMap.size());
    }

    @Test
    public void testCreateRoleNoDuplicateRoles() {
        roleHashMap.put("testRole", new Role("testRole"));
        assertTrue(roleHashMap.containsKey("testRole"));
        assertThrows(DuplicateRoleException.class, () -> roleManager.createRole("testRole"));
    }

    @Test
    public void testDeleteUserValidDelete() throws NoSuchRoleException {
        roleHashMap.put("testRole", new Role("testRole"));
        roleManager.deleteRole("testRole");
        assertEquals(0, roleHashMap.size());
    }

    @Test
    public void testDeleteUserUserDoesntExist() {
        assertThrows(NoSuchRoleException.class, () -> roleManager.deleteRole("testUser"));
    }

}
