package be.sgerard.i18n.model.security.repository;

import be.sgerard.i18n.model.security.auth.external.ExternalUserToken;
import lombok.Value;

import java.util.Optional;

/**
 * The current user to use when authenticated to the remote repository.
 *
 * @author Sebastien Gerard
 */
@Value
@SuppressWarnings("RedundantModifiersValueLombok")
public class CurrentUser {

    /**
     * The name to be displayed to the end-user (ideally composed of the first name, last name).
     */
    private final String displayName;

    /**
     * The user's email.
     */
    private final String email;

    /**
     * The token associated to the user (if authenticated externally).
     */
    private final ExternalUserToken externalToken;

    /**
     * @see #externalToken
     */
    public Optional<ExternalUserToken> getExternalToken() {
        return Optional.ofNullable(externalToken);
    }
}
