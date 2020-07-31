package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of authentication.
 *
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    /**
     * Returns the current {@link AuthenticatedUser authenticated user}.
     */
    Mono<AuthenticatedUser> getCurrentUser();

    /**
     * Returns the current {@link AuthenticatedUser authenticated user}.
     */
    default Mono<AuthenticatedUser> getCurrentUserOrDie() throws AccessDeniedException {
        return getCurrentUser()
                .switchIfEmpty(Mono.error(new AccessDeniedException("Please authenticate.")));
    }

    /**
     * Returns all the {@link AuthenticatedUser authenticated users}.
     */
    Flux<AuthenticatedUser> findAll();

    /**
     * Returns all the {@link AuthenticatedUser authentication} of the specified user.
     */
    Flux<AuthenticatedUser> findAll(String userId);

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
     * Updates all the {@link AuthenticatedUser users} linked to the specified {@link UserEntity user}.
     */
    Mono<Void> updateAuthentications(UserEntity user);

    /**
     * Deletes all the {@link AuthenticatedUser authenticated users} associated to the specified {@link UserEntity user}.
     */
    Mono<Void> deleteAllAuthentications(String userId);

}
