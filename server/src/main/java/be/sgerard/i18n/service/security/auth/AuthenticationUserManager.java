package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of {@link AuthenticatedUser authenticated users}.
 *
 * @author Sebastien Gerard
 */
public interface AuthenticationUserManager {

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
     * Creates the {@link AuthenticatedUser authenticated user} for the specified {@link ExternalUserDetails external user}.
     */
    Mono<ExternalAuthenticatedUser> createUser(ExternalUserDetails userDetails);

    /**
     * Creates the {@link AuthenticatedUser authenticated user} for the specified {@link InternalUserDetails internal user}.
     */
    Mono<InternalAuthenticatedUser> createUser(InternalUserDetails userDetails);

    /**
     * Updates the specified {@link AuthenticatedUser authenticated user}: sessions are updated + an event is emitted.
     */
    Mono<AuthenticatedUser> update(AuthenticatedUser authenticatedUser);

    /**
     * Deletes all the {@link AuthenticatedUser authenticated users} associated to the specified {@link UserEntity user}.
     *
     * @see UserEntity#getId()
     */
    Mono<Void> deleteAll(String userId);
}
