package com.alanpeinejr.manager;

import com.alanpeinejr.encryption.IEncrypt;
import com.alanpeinejr.exception.DuplicateUserException;
import com.alanpeinejr.exception.NoSuchUserException;

import java.time.Clock;
import java.util.Map;

public class UserManager {

    private final Map<String, User> userStorage;

    public UserManager(Map<String, User> userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(String username, String password, IEncrypt encryptor) throws DuplicateUserException {
        if (userStorage.containsKey(username)) {
            throw new DuplicateUserException("User already exists");
        }
        double salt = Math.random();
        User user = new User(username, encryptor.encrypt(password + salt), salt);
        userStorage.put(username, user);
        return user;
    }

    public void deleteUser(String username) throws NoSuchUserException {
        if (!userStorage.containsKey(username)) {
            throw new NoSuchUserException("No Such User Exists");
        }
        userStorage.remove(username);

    }

    public void addRoleToUser(User user, Role role) throws NoSuchUserException {
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        }
        if (!userStorage.containsKey(user.getUsername())) {
            throw new NoSuchUserException("Cannot add Role to a user that doesn't exist");
        }
        userStorage.put(user.getUsername(), user);
    }

    public void removeRoleFromAllUsers(Role role) {
        userStorage.forEach((s, user) -> user.getRoles().remove(role));
    }

    public void invalidate(String user) {
        User invalidUser = userStorage.get(user);
        if (invalidUser != null) {
            invalidUser.setLoginTime(null);
        }
    }

    public boolean authenticate(String username, String password, IEncrypt encryptor) throws NoSuchUserException {
        User user = userStorage.get(username);
        //user exists, and password is correct
        if (user != null && user.getPassword().equals(encryptor.encrypt(password + user.getSalt()))) {
            user.setLoginTime(Clock.systemUTC().instant());
            return true;
        } else if (user == null) {
            throw new NoSuchUserException("No Such User Exists");
        }
        return false;
    }

}
