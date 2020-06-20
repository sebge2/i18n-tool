package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.persistence.UserPreferencesEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of preferences.
 *
 * @author Sebastien Gerard
 */
public interface UserPreferencesListener {

    /**
     * Performs an action after the update of the specified preferences.
     */
    default Mono<Void> onUpdate(UserPreferencesEntity preferences) {
        return Mono.empty();
    }

}
