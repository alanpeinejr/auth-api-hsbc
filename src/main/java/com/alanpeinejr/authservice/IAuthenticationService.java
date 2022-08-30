package com.alanpeinejr.authservice;

import com.alanpeinejr.encryption.EncryptionManager;
import com.alanpeinejr.manager.Role;
import com.alanpeinejr.manager.RoleManager;
import com.alanpeinejr.manager.TokenManager;
import com.alanpeinejr.manager.User;
import com.alanpeinejr.manager.UserManager;

import java.util.Map;

public interface IAuthenticationService {


    void setUserStorage(Map<String, User> userStorage);

    void setRoleStorage(Map<String, Role> roleStorage);

    void setUserManager(UserManager userManager);

    void setRoleManager(RoleManager roleManager);

    void setTokenManager(TokenManager tokenManager);

    void setEncryptionManager(EncryptionManager encryptionManager);

}
