restx-ldap
=========
A restx module that allows to interact with an LDAP server in the context of user authentication.
The used LDAP client libray is Apache Directory which is compatible with all LDAP implementations

First we need to define two implementations : a user service (this component ensure authentication by retriving user by name, and by checking credentials) 
which uses a user repository (the data access layer) :

```java
   @Component
   public class MyLdapUserRepository extends LdapUserRepository<User> {
   
       public LdapUserPersistor(LdapConnection ldapConnection, LdapSettings ldapSettings, LdapUserDefinition userDefinition, User defaultAdmin) {
           super(ldapConnection, ldapSettings, userDefinition, defaultAdmin);
       }
   }
```
   
```java
   @Component
   public class MyLdapUserService extends LdapUserService<User> {
   
       public LdapUserService(LdapUserRepository userRepository, CredentialsStrategy credentialsStrategy) {
           super(userRepository, credentialsStrategy);
       }
   }
```

Finally an example of a LDAP module :

```java

@Module
public class SecurityModule {
    
    @Provides
    public BasicPrincipalAuthenticator authenticator(LdapUserService userService, SecuritySettings securitySettings) {
        return new StdBasicPrincipalAuthenticator(userService, securitySettings);
    }

    /**
    *  Ensure the mapping between an LDAP entry and a domain specific user. The
    *  interface can also resolve credentials from a specific LDAP entry's field 
    *  ('userPassword' by default). 
    */
    @Provides
    public LdapUserDefinition ldapUserDefinition() {
        return new LdapUserDefinition<User>() {
            @Override
            public User mapToPrincipal(Entry ldapEntry) {
                List<Role> roles = CommonUtils.stream(ldapEntry.get("businessCategory"))
                        .map(Value::getString)
                        .map(Role::valueOf)
                        .collect(Collectors.toList());
                return new User()
                        .setFirstName(ldapEntry.get("givenname").get().getString())
                        .setLastName(ldapEntry.get("sn").get().getString())
                        .setLogin(ldapEntry.get("uid").get().getString())
                        .setEmail(ldapEntry.get("mail").get().getString())
                        .setRoles(roles);
            }
        };
    }
}
```