package be.sgerard.i18n.service.security.auth.listener;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import reactor.core.publisher.Mono;

/**
 * Listener of {@link AuthenticatedUser authenticated user}.
 *
 * @author Sebastien Gerard
 */
public interface AuthenticatedUserListener {

    /**
     * Performs an action after the update of the specified authenticated user.
     */
    default Mono<Void> afterUpdate(AuthenticatedUser authenticatedUser) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified authenticated user.
     */
    default Mono<Void> afterDelete(AuthenticatedUser authenticatedUser) {
        return Mono.empty();
    }
}
