package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.repository.security.UserLiveSessionRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.security.session.listener.UserLiveSessionListener;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Implementation of the {@link UserLiveSessionManager user live session manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class UserLiveSessionManagerImpl implements UserLiveSessionManager {

    private final AuthenticationUserManager authenticationUserManager;
    private final UserManager userManager;
    private final UserLiveSessionRepository repository;
    private final UserLiveSessionListener listener;

    public UserLiveSessionManagerImpl(AuthenticationUserManager authenticationUserManager,
                                      UserManager userManager,
                                      UserLiveSessionRepository repository,
                                      UserLiveSessionListener listener) {
        this.authenticationUserManager = authenticationUserManager;
        this.userManager = userManager;
        this.repository = repository;
        this.listener = listener;
    }

    @Override
    public Flux<UserLiveSessionEntity> getCurrentLiveSessions() {
        return repository.findByLogoutTimeIsNull();
    }

    @Override
    public Mono<UserLiveSessionEntity> startSession() {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .flatMap(authenticatedUser -> userManager.findByIdOrDie(authenticatedUser.getUserId()))
                .flatMap(currentUser -> repository.save(new UserLiveSessionEntity(currentUser)))
                .flatMap(session ->
                        listener
                                .afterStarting(session)
                                .thenReturn(session)
                );
    }

    @Override
    public Mono<UserLiveSessionEntity> getSessionOrDie(String id) {
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.userLiveSessionNotFoundException(id)));
    }

    @Override
    public Mono<Void> stopSession(UserLiveSessionEntity session) {
        return getSessionOrDie(session.getId())
                .flatMap(currentSession -> {
                    if (currentSession.getLogoutTime() == null) {
                        currentSession.setLogoutTime(Instant.now());
                    }

                    return repository.save(currentSession);
                })
                .flatMap(listener::afterStopping);
    }

    @Override
    public Mono<Void> deleteSession(UserLiveSessionEntity session) {
        return repository
                .delete(session)
                .then(listener.afterDeletion(session));
    }

    @Override
    public Mono<Void> deleteAll(String user) {
        return repository
                .findByUser(user)
                .flatMap(this::deleteSession)
                .then();
    }
}
