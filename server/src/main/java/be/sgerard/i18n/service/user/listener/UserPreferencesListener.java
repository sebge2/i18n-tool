package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of preferences.
 *
 * @author Sebastien Gerard
 */
public interface UserPreferencesListener {

    /**
     * Performs an action after the update of the specified user's preferences.
     */
    default Mono<Void> afterUpdate(UserEntity user) {
        return Mono.empty();
    }

}
