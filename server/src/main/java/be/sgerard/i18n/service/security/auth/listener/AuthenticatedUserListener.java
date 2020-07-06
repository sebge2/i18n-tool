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
     * Performs an action after the creation of the specified authenticated user.
     */
    default Mono<Void> onCreate(AuthenticatedUser authenticatedUser) {
        return Mono.empty();
    }

    /**
     * Performs an action after the update of the specified authenticated user.
     */
    default Mono<Void> onUpdate(AuthenticatedUser authenticatedUser) {
        return Mono.empty();
    }
}
