package restx.ldap;

import com.google.common.collect.Lists;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import restx.security.RestxPrincipal;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface LdapUserDefinition<U extends RestxPrincipal> {

    default List<String> resolvePasswords(Entry entry) {
        Attribute passwordAttribute = entry.get("userPassword");
        if (passwordAttribute != null) {
            return StreamSupport.stream(passwordAttribute.spliterator(), false)
                    .map(Value::getString)
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    U mapToPrincipal(Entry ldapEntry);
}
