package be.sgerard.i18n.model.security.user;

import lombok.Builder;
import lombok.Getter;

/**
 * User authenticated externally.
 *
 * @author Sebastien Gerard
 */
@Getter
@Builder(builderClassName = "Builder")
public class ExternalUser {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * The unique id of the user in the external system.
     */
    private final String externalId;

    /**
     * The {@link ExternalAuthSystem client} used to authenticate the user.
     */
    private final ExternalAuthSystem authSystem;

    /**
     * The username.
     */
    private final String username;

    /**
     * The name to be displayed.
     */
    private final String displayName;

    /**
     * The user's email.
     */
    private final String email;

    /**
     * The URL of the user's avatar.
     */
    private final String avatarUrl;

    /**
     * Flag indicating whether the user is authenticated, but not authorized the application.
     */
    private final boolean authorized;

    @Override
    public String toString() {
        return "ExternalUser(" + username + ", " + email + ')';
    }

    /**
     * Builder of {@link ExternalUser external user}.
     */
    public static final class Builder {
    }
}
