package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.auth.context.AuthenticationUserContextProcessor;
import be.sgerard.i18n.service.security.auth.listener.AuthenticatedUserListener;
import be.sgerard.i18n.service.security.session.repository.SessionRepository;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static be.sgerard.i18n.model.security.auth.AuthenticatedUser.ROLE_USER;
import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.getAuthenticatedUser;
import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.setAuthenticatedUser;
import static java.util.Collections.singleton;

/**
 * Implementation of the {@link AuthenticationUserManager authentication manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationUserManagerImpl implements AuthenticationUserManager {

    private final SessionRepository sessionRepository;
    private final AuthenticationUserContextProcessor contextProcessor;
    private final AuthenticatedUserListener listener;

    public AuthenticationUserManagerImpl(SessionRepository sessionRepository,
                                         AuthenticationUserContextProcessor contextProcessor,
                                         AuthenticatedUserListener listener) {
        this.sessionRepository = sessionRepository;
        this.contextProcessor = contextProcessor;
        this.listener = listener;
    }

    @Override
    public Mono<AuthenticatedUser> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> Mono.justOrEmpty(getAuthenticatedUser(securityContext.getAuthentication())));
    }

    @Override
    public Flux<AuthenticatedUser> findAll() {
        return sessionRepository
                .findAll()
                .flatMap(session -> Mono.justOrEmpty(getAuthenticatedUser(session)));
    }

    @Override
    public Flux<AuthenticatedUser> findAll(String userId) {
        return findAll()
                .filter(authenticatedUser -> Objects.equals(userId, authenticatedUser.getUserId()));
    }

    @Override
    public Mono<ExternalAuthenticatedUser> createUser(ExternalUserDetails userDetails) {
        final ExternalAuthenticatedUser authenticatedUser = new ExternalAuthenticatedUser(
                UUID.randomUUID().toString(),
                userDetails.getId(),
                userDetails.getToken(),
                userDetails.getRoles(),
                userDetails.getDisplayName(),
                userDetails.getEmail(),
                singleton(ROLE_USER),
                new HashMap<>()
        );

        return contextProcessor
                .onCreate(authenticatedUser)
                .map(authenticatedUser::updateContext)
                .map(ExternalAuthenticatedUser.class::cast);
    }

    @Override
    public Mono<InternalAuthenticatedUser> createUser(InternalUserDetails userDetails) {
        final InternalAuthenticatedUser authenticatedUser = new InternalAuthenticatedUser(
                UUID.randomUUID().toString(),
                userDetails.getId(),
                userDetails.getRoles(),
                userDetails.getDisplayName(),
                userDetails.getEmail(),
                singleton(ROLE_USER),
                new HashMap<>()
        );

        return contextProcessor
                .onCreate(authenticatedUser)
                .map(authenticatedUser::updateContext)
                .map(InternalAuthenticatedUser.class::cast);
    }

    @Override
    public Mono<AuthenticatedUser> update(AuthenticatedUser authenticatedUser) {
        return findSession(authenticatedUser)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.authenticatedUserNotFoundException(authenticatedUser.getId())))
                .doOnNext(session -> setAuthenticatedUser(authenticatedUser, session))
//                .flatMap(sessionRepository::save) TODO
                .then(listener.afterUpdate(authenticatedUser).thenReturn(authenticatedUser));
    }

    @Override
    public Mono<Void> deleteAll(String userId) {
        return this
                .findAll(userId)
                .flatMap(this::delete)
                .then();
    }

    /**
     * Returns the {@link WebSession session} for the specified user.
     */
    private Mono<MongoSession> findSession(AuthenticatedUser authenticatedUser) {
        return sessionRepository
                .findAll()
                .flatMap(session ->
                        Mono
                                .justOrEmpty(getAuthenticatedUser(session))
                                .filter(sessionAuthUser -> Objects.equals(authenticatedUser.getId(), sessionAuthUser.getId()))
                                .map(auth -> session)
                )
                .next();
    }

    /**
     * Deletes the {@link AuthenticatedUser authentication user}: invalidates sessions + an event is emitted.
     */
    private Mono<AuthenticatedUser> delete(AuthenticatedUser authenticatedUser) {
        return findSession(authenticatedUser)
                .flatMap(webSession -> sessionRepository.deleteById(webSession.getId()))
                .then(
                        listener.afterDelete(authenticatedUser)
                                .thenReturn(authenticatedUser)
                );
    }
}
