package restx.ldap.rest;

import restx.annotations.GET;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.ldap.LdapUserRepository;
import restx.security.RestxPrincipal;

@Component
@RestxResource("/ldap")
public class LdapResource {

    private final LdapUserRepository ldapUserRepository;

    public LdapResource(LdapUserRepository ldapUserRepository) {
        this.ldapUserRepository = ldapUserRepository;
    }

    @GET("/users")
    public Iterable<RestxPrincipal> findAllUsers() {
        return ldapUserRepository.findUsers();
    }
}
