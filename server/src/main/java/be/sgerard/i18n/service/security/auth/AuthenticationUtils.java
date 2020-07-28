package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/**
 * Bunch of utility methods for authentication.
 *
 * @author Sebastien Gerard
 */
final class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    /**
     * Returns the current {@link AuthenticatedUser authenticated user} from the specified {@link Authentication authentication}.
     */
    static Optional<AuthenticatedUser> getAuthenticatedUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof AuthenticatedUser) {
            return Optional.of((AuthenticatedUser) authentication.getPrincipal());
        } else {
            return Optional.empty();
        }
    }

}
