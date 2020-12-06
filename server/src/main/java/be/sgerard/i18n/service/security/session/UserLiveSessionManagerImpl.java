package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.repository.security.UserLiveSessionRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.security.session.listener.UserLiveSessionListener;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<UserLiveSessionEntity> startSession() {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .flatMap(authenticatedUser -> userManager.findByIdOrDie(authenticatedUser.getUserId()))
                .flatMap(currentUser -> repository.save(new UserLiveSessionEntity(currentUser)))
                .flatMap(session ->
                        listener
                                .onNewSession(session)
                                .thenReturn(session)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserLiveSessionEntity> getSessionOrDie(String id) {
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.userLiveSessionNotFoundException(id)));
    }

    @Override
    @Transactional
    public Mono<Void> stopSession(UserLiveSessionEntity session) {
        return getSessionOrDie(session.getId())
                .flatMap(currentSession -> {
                    if (currentSession.getLogoutTime() == null) {
                        currentSession.setLogoutTime(Instant.now());
                    }

                    return repository.save(currentSession);
                })
                .flatMap(listener::onStopSession);
    }

    @Override
    @Transactional
    public Mono<Void> deleteSession(UserLiveSessionEntity session) {
        return repository
                .delete(session)
                .then(listener.onDeletedSession(session));
    }

    @Override
    @Transactional
    public Mono<Void> deleteAll(UserEntity userEntity) {
        return repository
                .findByUser(userEntity)
                .flatMap(this::deleteSession)
                .then();
    }
}
