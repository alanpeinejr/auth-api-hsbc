package com.alanpeinejr.authservice;

import com.alanpeinejr.encryption.EncryptionManager;
import com.alanpeinejr.manager.*;

import java.util.Map;

/**
 * Builder class to create AuthenticationService from an easy-to-use API, must call .build() at the end
 */
public class AuthenticationServiceBuilder {

    private AuthenticationService service;

    public AuthenticationService build(){
        return this.service;
    }

    public AuthenticationServiceBuilder createAuthenticationService() {
        service = new AuthenticationService();
        return this;
    }
    public AuthenticationServiceBuilder setupUserStorage(Map<String, User> users){
        service.setUserStorage(users);
        return this;
    }
    public  AuthenticationServiceBuilder setupRoleStorage(Map<String, Role> roles){
        service.setRoleStorage(roles);
        return this;
    }
    public  AuthenticationServiceBuilder userManager(UserManager userManager){
        service.setUserManager(userManager);
        return this;
    }
    public  AuthenticationServiceBuilder roleManager(RoleManager roleManager){
        service.setRoleManager(roleManager);
        return this;
    }
    public  AuthenticationServiceBuilder tokenManager(TokenManager tokenManager){
        service.setTokenManager(tokenManager);
        return this;
    }
    public  AuthenticationServiceBuilder encryptionManager(EncryptionManager encryptionManager){
        service.setEncryptionManager(encryptionManager);
        return this;
    }

}
