package restx.ldap;

import restx.config.Settings;
import restx.config.SettingsKey;

@Settings
public interface LdapSettings {

    @SettingsKey(key = "ldap.domain", defaultValue = "dc=example,dc=org")
    String domain();

    @SettingsKey(key = "ldap.connect.host", defaultValue = "localhost")
    String host();

    @SettingsKey(key = "ldap.connect.port", defaultValue = "389")
    String port();

    @SettingsKey(key = "ldap.connect.identifier", defaultValue = "cn=admin,dc=example,dc=org")
    String identifier();

    @SettingsKey(key = "ldap.connect.password", defaultValue = "admin")
    String password();

    @SettingsKey(key = "ldap.bind.idField", defaultValue = "uid")
    String idField();
}
