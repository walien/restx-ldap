package restx.ldap;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restx.security.CredentialsStrategy;
import restx.security.RestxPrincipal;
import restx.security.UserService;

public class LdapUserService<U extends RestxPrincipal> implements UserService<U> {

    private static final Logger logger = LoggerFactory.getLogger(LdapUserService.class);

    private final LdapUserRepository<U> userRepository;
    private final CredentialsStrategy checker;

    public LdapUserService(LdapUserRepository userRepository, CredentialsStrategy credentialsStrategy) {
        this.userRepository = userRepository;
        this.checker = credentialsStrategy;
    }

    @Override
    public Optional<U> findUserByName(String name) {
        return userRepository.findUserByName(name);
    }

    @Override
    public Optional<U> findAndCheckCredentials(String name, String passwordHash) {
        Optional<U> user = findUserByName(name);
        if (user.isPresent()) {
            boolean credentialMatches = userRepository.findCredentialsByUserName(name)
                    .stream()
                    .anyMatch(storedPassword -> checker.checkCredentials(name, passwordHash, storedPassword));
            if (credentialMatches) {
                return user;
            }
        }
        return Optional.absent();
    }
}
