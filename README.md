restx-ldap
=========
A restx module that allows to interact with an LDAP server in the context of user authentication.
The used LDAP client libray is Apache Directory which is compatible with all LDAP implementations
   


Example :

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