package be.sgerard.i18n.model.security.auth.external;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Raw information about a user coming from an external authentication system.
 *
 * @author Sebastien Gerard
 */
@Getter
public class RawExternalUser {

    /**
     * The name of the authentication system.
     */
    private final ExternalAuthSystem authSystem;

    /**
     * The OAuth token.
     */
    private final String token;

    /**
     * User's attributes.
     */
    private final Map<String, Object> attributes;

    @SuppressWarnings("Java9CollectionFactory")
    public RawExternalUser(ExternalAuthSystem authSystem, String token, Map<String, Object> attributes) {
        this.authSystem = authSystem;
        this.token = token;
        this.attributes = unmodifiableMap(new HashMap<>(attributes));
    }
}
