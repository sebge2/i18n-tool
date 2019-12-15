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
     * Performs an action when creating the specified session.
     */
    default Mono<Void> onNewSession(UserLiveSessionEntity session) {
        return Mono.empty();
    }

    /**
     * Performs an action when stopping the specified session.
     */
    default Mono<Void> onStopSession(UserLiveSessionEntity session) {
        return Mono.empty();
    }

    /**
     * Performs an action when deleting (physically) the specified session.
     */
    default Mono<Void> onDeletedSession(UserLiveSessionEntity session) {
        return Mono.empty();
    }
}
