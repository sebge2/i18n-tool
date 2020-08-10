package be.sgerard.i18n.model.security.user;

import be.sgerard.i18n.model.security.auth.external.RawExternalUser;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * All supported external authentication systems.
 *
 * @author Sebastien Gerard
 */
public enum ExternalAuthSystem {

    /**
     * @see RawExternalUser#getAuthSystem()
     */
    OAUTH_GOOGLE("Google"),

    /**
     * @see RawExternalUser#getAuthSystem()
     */
    OAUTH_GITHUB("GitHub");

    private final String name;

    ExternalAuthSystem(String name) {
        this.name = name;
    }

    /**
     * Maps from the system name.
     */
    public static ExternalAuthSystem fromName(String name){
        return Stream
                .of(ExternalAuthSystem.values())
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
