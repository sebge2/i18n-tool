package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of users.
 *
 * @author Sebastien Gerard
 */
public interface UserListener {

    /**
     * Performs an action after the specified user has been persisted.
     */
    default Mono<Void> afterPersist(UserEntity user) {
        return Mono.empty();
    }

    /**
     * Performs an action before persisting the update of the specified user.
     */
    default Mono<Void> beforeUpdate(UserEntity user) {
        return Mono.empty();
    }

    /**
     * Performs an action after the update of the specified user.
     */
    default Mono<Void> afterUpdate(UserEntity user) {
        return Mono.empty();
    }

    /**
     * Performs an action before the deletion of the specified user.
     */
    default Mono<Void> beforeDelete(UserEntity user) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified user.
     */
    default Mono<Void> afterDelete(UserEntity user) {
        return Mono.empty();
    }
}
