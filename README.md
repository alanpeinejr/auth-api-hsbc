# auth-api-hsbc
Code interview for HSBC Innovation Lab -- a simple authentication/authorization token api

## API
Javadocs for the API are located primarily within AbstractAuthenticationService. Written in Java 11
### Assumptions
1. API is for code use only, not a web app
2. Encryption is not a focus. I ended up going with an extremely simple "encryption" method. This would be the number one place to improve, but I felt justified in my assumption here as it is mentioned in the description that the wire is safe. Being entirely in memory also led to this decision.

### Choices
1. Built a service builder for ease of creating the actual service. All fields in it are required for proper building.
2. Hand mocked for tests. Wanted to keep dependencies to a complete minimum
3. Written in a way for ease of testing. Why the Managers are not the sole owners of the storage
4. Disabled Access to external ways to view rolls in Users, and really any way to modify the User object because I felt it went against the whole point of other parts of the API.
5. Added some errors where I felt necessary. ie, You cant add a role to a User that doesn't exist
6. Left things I disagreed with in place, but noted in the docs.
## Dependencies

Java 11 becuase its the SDK I had installed, dont think theres actually anything specific to the version but listing here to be certain. <br>
GSON for simple java class serialization into json, for easy to create tokens <br>
JUnit for writing test cases

## Tests

### Unit Tests
    
    EncryptionManagerTest
    RoleManagerTest
    TokenManagerTest
    UserManagerTest

### Integration Tests
    AuthenticationServiceTest
