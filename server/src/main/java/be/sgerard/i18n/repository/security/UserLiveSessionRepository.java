package be.sgerard.i18n.repository.security;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link ReactiveMongoRepository Repository} of {@link UserLiveSessionEntity user-live-session} entities.
 *
 * @author Sebastien Gerard
 */
public interface UserLiveSessionRepository extends ReactiveMongoRepository<UserLiveSessionEntity, String> {

    /**
     * Finds the user-live-session by its {@link UserLiveSessionEntity#getSimpSessionId() SIMP session id}.
     */
    Mono<UserLiveSessionEntity> findBySimpSessionId(String id);

    /**
     * Finds the user-live-sessions that are still active (no {@link UserLiveSessionEntity#getLogoutTime() logout}).
     */
    Flux<UserLiveSessionEntity> findByLogoutTimeIsNull();

    /**
     * Finds the user-live-session by their {@link UserLiveSessionEntity#getAuthenticatedUserId() authenticated user ids}.
     */
    Flux<UserLiveSessionEntity> findByAuthenticatedUserId(String authenticatedUserId);

}
