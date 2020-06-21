package be.sgerard.i18n.model.security.user.persistence;

import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * All supported external authentication clients.
 *
 * @author Sebastien Gerard
 */
public enum ExternalAuthClient {

    /**
     * @see OAuthExternalUser#getOauthClient()
     */
    GOOGLE("Google"),

    /**
     * @see OAuthExternalUser#getOauthClient()
     */
    GITHUB("GitHub");

    private final String name;

    ExternalAuthClient(String name) {
        this.name = name;
    }

    /**
     * Maps from the client name.
     */
    public static ExternalAuthClient fromName(String name){
        return Stream
                .of(ExternalAuthClient.values())
                .filter(client -> Objects.equals(client.getName(), name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no client named [" + name + "]."));
    }

    /**
     * Returns the unique name of the client.
     */
    public String getName() {
        return name;
    }
}
