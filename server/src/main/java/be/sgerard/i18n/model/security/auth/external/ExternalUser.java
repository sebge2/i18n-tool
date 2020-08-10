package be.sgerard.i18n.model.security.auth.external;

import be.sgerard.i18n.model.security.user.ExternalAuthSystem;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * User coming from an external authentication system.
 *
 * @author Sebastien Gerard
 */
public class ExternalUser {

    private final ExternalAuthSystem authSystem;
    private final String token;
    private final Map<String, Object> attributes;

    @SuppressWarnings("Java9CollectionFactory")
    public ExternalUser(ExternalAuthSystem authSystem, String token, Map<String, Object> attributes) {
        this.authSystem = authSystem;
        this.token = token;
        this.attributes = unmodifiableMap(new HashMap<>(attributes));
    }

    /**
     * Returns the name of the authentication system.
     */
    public ExternalAuthSystem getAuthSystem() {
        return authSystem;
    }

    /**
     * Returns the OAuth token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns user's attributes.
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
