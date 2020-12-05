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
     * Performs an action after the creation of the specified user.
     */
    default Mono<Void> afterCreate(UserEntity user) {
        return Mono.empty();
    }

    /**
     * Performs an action after the update of the specified user.
     */
    default Mono<Void> afterUpdate(UserEntity user) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified user.
     */
    default Mono<Void> afterDelete(UserEntity user) {
        return Mono.empty();
    }
}
