package restx.ldap;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restx.security.RestxPrincipal;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LdapUserRepository<U extends RestxPrincipal> {

    private static final Logger logger = LoggerFactory.getLogger(LdapUserRepository.class);

    private final LdapConnection ldapConnection;
    private final LdapSettings ldapSettings;
    private final LdapUserDefinition<U> userDefinition;
    private final U defaultAdmin;

    public LdapUserRepository(LdapConnection ldapConnection, LdapSettings ldapSettings, LdapUserDefinition userDefinition, U defaultAdmin) {
        this.ldapConnection = ldapConnection;
        this.ldapSettings = ldapSettings;
        this.userDefinition = userDefinition;
        this.defaultAdmin = defaultAdmin;
    }

    private Stream<Entry> searchUser(String name) {
        try {
            String base = ldapSettings.domain();
            EntryCursor search = ldapConnection.search(base, String.format("(%s=%s)", ldapSettings.idField(), name), SearchScope.SUBTREE);
            return StreamSupport.stream(search.spliterator(), false);
        } catch (LdapException e) {
            logger.error("unable to execute ldap query", e);
        }
        return Stream.of();
    }

    public List<U> findUsers() {
        return searchUser("*")
                .map(userDefinition::<U>mapToPrincipal)
                .collect(Collectors.toList());
    }

    public Optional<U> findUserByName(String name) {
        return searchUser(name)
                .findFirst()
                .map(userDefinition::<U>mapToPrincipal)
                .map(Optional::of)
                .orElseGet(Optional::absent);
    }

    public List<String> findCredentialsByUserName(String name) {
        return searchUser(name)
                .map(userDefinition::resolvePasswords)
                .findFirst()
                .orElse(Lists.newArrayList());
    }

    public boolean isAdminDefined() {
        return true;
    }

    public U defaultAdmin() {
        return defaultAdmin;
    }
}
