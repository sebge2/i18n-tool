package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.listener.AuthenticatedUserListener;
import be.sgerard.i18n.service.security.repository.RepositoryCredentialsManager;
import be.sgerard.i18n.service.security.session.repository.SessionRepository;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static be.sgerard.i18n.model.security.auth.AuthenticatedUser.ROLE_USER;
import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.getAuthenticatedUser;
import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.setAuthenticatedUser;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

/**
 * Implementation of the {@link AuthenticationUserManager authentication manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationUserManagerImpl implements AuthenticationUserManager {

    private final RepositoryCredentialsManager repositoryCredentialsManager;
    private final SessionRepository sessionRepository;
    private final AuthenticatedUserListener listener;

    public AuthenticationUserManagerImpl(SessionRepository sessionRepository,
                                         RepositoryCredentialsManager repositoryCredentialsManager,
                                         AuthenticatedUserListener listener) {
        this.repositoryCredentialsManager = repositoryCredentialsManager;
        this.sessionRepository = sessionRepository;
        this.listener = listener;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuthenticatedUser> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> Mono.justOrEmpty(getAuthenticatedUser(securityContext.getAuthentication())));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuthenticatedUser> findAll() {
        return sessionRepository
                .findAll()
                .flatMap(session -> Mono.justOrEmpty(getAuthenticatedUser(session)));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuthenticatedUser> findAll(String userId) {
        return findAll()
                .filter(authenticatedUser -> Objects.equals(userId, authenticatedUser.getUserId()));
    }

    @Override
    @Transactional
    public Mono<ExternalAuthenticatedUser> createUser(ExternalUserDetails userDetails) {
        return repositoryCredentialsManager
                .loadCredentials(userDetails)
                .collectList()
                .map(repositoryCredentials ->
                        new ExternalAuthenticatedUser(
                                UUID.randomUUID().toString(),
                                userDetails.getId(),
                                userDetails.getToken(),
                                userDetails.getRoles(),
                                singleton(ROLE_USER),
                                repositoryCredentials
                        )
                );
    }

    @Override
    public Mono<InternalAuthenticatedUser> createUser(InternalUserDetails userDetails) {
        return repositoryCredentialsManager
                .loadCredentials(userDetails)
                .collectList()
                .map(repositoryCredentials ->
                        new InternalAuthenticatedUser(
                                UUID.randomUUID().toString(),
                                userDetails.getId(),
                                userDetails.getRoles(),
                                singleton(ROLE_USER),
                                repositoryCredentials
                        )
                );
    }

    @Override
    public Mono<Void> updateAll(String userId, Collection<UserRole> roles) {
        return this
                .findAll(userId)
                .map(authenticatedUser -> authenticatedUser.updateSessionRoles(
                        Stream
                                .concat(
                                        authenticatedUser.getRoles().stream().filter(role -> !role.isAssignableByEndUser()),
                                        roles.stream()
                                )
                                .collect(toList())
                ))
                .flatMap(this::update)
                .then();
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
     * Updates the specified {@link AuthenticatedUser authenticated user}: sessions are updated + an event is emitted.
     */
    private Mono<AuthenticatedUser> update(AuthenticatedUser authenticatedUser) {
        return findSession(authenticatedUser)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.authenticatedUserNotFoundException(authenticatedUser.getId())))
                .doOnNext(session -> setAuthenticatedUser(authenticatedUser, session))
                .flatMap(sessionRepository::save)
                .then(
                        listener
                                .onUpdate(authenticatedUser)
                                .thenReturn(authenticatedUser)
                );
    }

    /**
     * Deletes the {@link AuthenticatedUser authentication user}: invalidates sessions + an event is emitted.
     */
    private Mono<AuthenticatedUser> delete(AuthenticatedUser authenticatedUser) {
        return findSession(authenticatedUser)
                .flatMap(webSession -> sessionRepository.deleteById(webSession.getId()))
                .then(
                        listener.onDelete(authenticatedUser)
                                .thenReturn(authenticatedUser)
                );
    }
}
