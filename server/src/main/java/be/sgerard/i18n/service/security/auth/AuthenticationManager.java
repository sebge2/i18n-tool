package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Manager of authentication.
 *
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    /**
     * Creates the {@link ExternalAuthenticatedUser authenticated user} for the specified {@link OAuthExternalUser OAuth user}.
     */
    Mono<ExternalAuthenticatedUser> createAuthentication(OAuthExternalUser externalUser);

    /**
     * Creates the {@link InternalAuthenticatedUser authenticated user} for the specified username. The password
     * has not been checked and must be checked later on.
     */
    // TODO in the other method the user is authenticated, find a better way
    Mono<InternalAuthenticatedUser> createAuthentication(String username);

    /**
     * Returns the current {@link AuthenticatedUser authenticated user}.
     */
    Optional<AuthenticatedUser> getCurrentUser();

    /**
     * Returns the current {@link AuthenticatedUser authenticated user}.
     */
    default AuthenticatedUser getCurrentUserOrDie() throws AccessDeniedException {
        return getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Please authenticate."));
    }

}
