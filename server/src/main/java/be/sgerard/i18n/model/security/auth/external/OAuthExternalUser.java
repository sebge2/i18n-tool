package be.sgerard.i18n.model.security.auth.external;

import be.sgerard.i18n.model.security.user.persistence.ExternalAuthSystem;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * User coming from OAuth.
 *
 * @author Sebastien Gerard
 */
public class OAuthExternalUser {

    private final ExternalAuthSystem oauthClient;
    private final String token;
    private final Map<String, Object> attributes;

    @SuppressWarnings("Java9CollectionFactory")
    public OAuthExternalUser(ExternalAuthSystem oauthClient, String token, Map<String, Object> attributes) {
        this.oauthClient = oauthClient;
        this.token = token;
        this.attributes = unmodifiableMap(new HashMap<>(attributes));
    }

    /**
     * Returns the name of the OAuth client (used in the application configuration files).
     */
    public ExternalAuthSystem getOauthClient() {
        return oauthClient;
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
