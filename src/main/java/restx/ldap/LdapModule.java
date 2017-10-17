package restx.ldap;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.slf4j.Logger;
import restx.factory.AutoStartable;
import restx.factory.Module;
import restx.factory.Provides;

import static org.slf4j.LoggerFactory.getLogger;

@Module
public class LdapModule {

    private static final Logger logger = getLogger(LdapModule.class);

    @Provides
    public LdapConnection ldapConnection(LdapSettings ldapSettings) {
        return new LdapNetworkConnection(ldapSettings.host(), Integer.parseInt(ldapSettings.port()));
    }

    @Provides
    public AutoStartable bindLdapConnection(LdapConnection connection, LdapSettings ldapSettings) {
        return () -> {
            try {
                connection.bind(ldapSettings.identifier(), ldapSettings.password());
                logger.info("LDAP - Connection has been successfuly established");
            } catch (LdapException e) {
                logger.error("error occured during LDAP server connection establishment", e);
            }
        };
    }

    @Provides
    public AutoCloseable closeLdapConnection(LdapConnection connection) {
        return () -> {
            try {
                connection.unBind();
                connection.close();
                logger.info("LDAP - Connection has been successfuly destroyed");
            } catch (LdapException e) {
                logger.error("error occured during LDAP server connection shutdown", e);
            }
        };
    }
}
