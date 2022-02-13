package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of {@link UserLiveSessionEntity user live sessions}.
 *
 * @author Sebastien Gerard
 */
public interface UserLiveSessionManager {

    /**
     * Returns all the current {@link UserLiveSessionEntity user live session}.
     */
    Flux<UserLiveSessionEntity> getCurrentLiveSessions();

    /**
     * Creates a new {@link UserLiveSessionEntity session} for the current user.
     */
    Mono<UserLiveSessionEntity> startSession();

    /**
     * Returns the specified {@link UserLiveSessionEntity session} having the specified id.
     */
    Mono<UserLiveSessionEntity> getSessionOrDie(String id);

    /**
     * Stops the specified {@link UserLiveSessionEntity session}.
     */
    Mono<Void> stopSession(UserLiveSessionEntity userLiveSession);

    /**
     * Deletes the specified {@link UserLiveSessionEntity session}.
     */
    Mono<Void> deleteSession(UserLiveSessionEntity userLiveSession);

    /**
     * Deletes all {@link UserLiveSessionEntity sessions} of the specified user.
     */
    default Mono<Void> deleteAll(UserEntity userEntity){
        return deleteAll(userEntity.getId());
    }

    /**
     * Deletes all {@link UserLiveSessionEntity sessions} of the specified user.
     */
    Mono<Void> deleteAll(String user);

}
