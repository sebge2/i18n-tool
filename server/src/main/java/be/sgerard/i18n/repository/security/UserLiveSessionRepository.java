package be.sgerard.i18n.repository.security;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * {@link ReactiveMongoRepository Repository} of {@link UserLiveSessionEntity user-live-session} entities.
 *
 * @author Sebastien Gerard
 */
public interface UserLiveSessionRepository extends ReactiveMongoRepository<UserLiveSessionEntity, String> {

    /**
     * Finds the user-live-sessions that are still active (no {@link UserLiveSessionEntity#getLogoutTime() logout}).
     */
    Flux<UserLiveSessionEntity> findByLogoutTimeIsNull();

    /**
     * Returns all sessions owned by the specified {@link UserEntity#getId() user}.
     */
    Flux<UserLiveSessionEntity> findByUser(String user);

}
