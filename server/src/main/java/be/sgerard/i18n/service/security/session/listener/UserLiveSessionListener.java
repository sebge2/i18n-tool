package be.sgerard.i18n.service.security.session.listener;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of the lifecycle of {@link UserLiveSessionEntity user live-sessions}.
 *
 * @author Sebastien Gerard
 */
public interface UserLiveSessionListener {

    /**
     * Performs an action when the specified session has been persisted.
     */
    default Mono<Void> afterStarting(UserLiveSessionEntity session) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified session has been stopped and persisted.
     */
    default Mono<Void> afterStopping(UserLiveSessionEntity session) {
        return Mono.empty();
    }

    /**
     * Performs an action after the specified session has been deleted.
     */
    default Mono<Void> afterDeletion(UserLiveSessionEntity session) {
        return Mono.empty();
    }
}
