package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.repository.security.UserLiveSessionRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import be.sgerard.i18n.service.security.session.listener.UserLiveSessionListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static be.sgerard.i18n.model.event.EventType.UPDATED_CURRENT_AUTHENTICATED_USER;

/**
 * Implementation of the {@link UserLiveSessionManager user live session manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class UserLiveSessionManagerImpl implements UserLiveSessionManager {

    private final AuthenticationManager authenticationManager;
    private final UserLiveSessionRepository repository;
    private final UserLiveSessionListener listener;

    public UserLiveSessionManagerImpl(UserManager userManager,
                                      UserLiveSessionManagerRepository repository,
                                      EventService eventService) {
        this.userManager = userManager;
        this.repository = repository;
        this.eventService = eventService;
//        this.eventService.addListener(new UpdatedAuthenticationUserListener());
        this.repository = repository;
        this.listener = listener;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserLiveSessionEntity> getCurrentLiveSessions() {
        return repository.findByLogoutTimeIsNull();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<UserLiveSessionEntity> startSession() {
        return authenticationManager
                .getCurrentUserOrDie()
                .flatMap(authenticatedUser -> repository.save(new UserLiveSessionEntity(authenticatedUser)));
    }

    @Override
    public Mono<UserLiveSessionEntity> getSessionOrDie(String id) {
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.translationNotFoundException(id))); // wrong exception
    }

    @Override
    public Mono<Void> stopSession(UserLiveSessionEntity userLiveSession) {
        return getSessionOrDie(userLiveSession.getId())
                .flatMap(currentSession -> {
                    if (currentSession.getLogoutTime() == null) {
                        currentSession.setLogoutTime(Instant.now());
                    }

                    return repository.save(currentSession);
                })
                .flatMap(session -> listener.onStopSession(userLiveSession));
    }
}
