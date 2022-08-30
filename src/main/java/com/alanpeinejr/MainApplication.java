package com.alanpeinejr;


import com.alanpeinejr.authservice.AuthenticationService;
import com.alanpeinejr.authservice.AuthenticationServiceBuilder;
import com.alanpeinejr.encryption.EncryptionManager;
import com.alanpeinejr.manager.Role;
import com.alanpeinejr.manager.RoleManager;
import com.alanpeinejr.manager.TokenManager;
import com.alanpeinejr.manager.User;
import com.alanpeinejr.manager.UserManager;

import java.util.HashMap;

public class MainApplication {
    //currently set to 2 hours
    private final static int TOKEN_LIFETIME_IN_SECONDS = 2 * 60 * 60;

    public static void main(String[] args) {

        AuthenticationService service = setupService();

        //TODO this is where I'd setup a web service to provide the microservice via a REST api
        //Not going to implement REST w/o Spring/jakarta
    }

    private static AuthenticationService setupService() {
        HashMap<String, User> userHashMap = new HashMap<>();
        HashMap<String, Role> roleHashMap = new HashMap<>();
        return new AuthenticationServiceBuilder()
                .createAuthenticationService()
                .setupUserStorage(userHashMap)
                .setupRoleStorage(roleHashMap)
                .userManager(new UserManager(userHashMap))
                .roleManager(new RoleManager(roleHashMap))
                .tokenManager(new TokenManager(TOKEN_LIFETIME_IN_SECONDS))
                .encryptionManager(new EncryptionManager())
                .build();
    }

}
