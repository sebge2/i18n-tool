package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.security.auth.external.ExternalUserRepositoryCredentialsHandler;
import be.sgerard.i18n.service.security.auth.external.OAuthUserMapper;
import be.sgerard.i18n.service.security.auth.listener.AuthenticatedUserListener;
import be.sgerard.i18n.service.security.session.repository.SessionRepository;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.getAuthenticatedUser;
import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.setAuthenticatedUser;
import static java.util.stream.Collectors.toList;
import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

/**
 * Implementation of the {@link AuthenticationManager authentication manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final UserManager userManager;
    private final RepositoryManager repositoryManager;
    private final SessionRepository sessionRepository;
    private final OAuthUserMapper externalUserHandler;
    private final ExternalUserRepositoryCredentialsHandler credentialsHandler;
    private final AuthenticatedUserListener listener;

    public AuthenticationManagerImpl(UserManager userManager,
                                     RepositoryManager repositoryManager,
                                     SessionRepository sessionRepository,
                                     OAuthUserMapper externalUserHandler,
                                     ExternalUserRepositoryCredentialsHandler credentialsHandler,
                                     AuthenticatedUserListener listener) {
        this.userManager = userManager;
        this.repositoryManager = repositoryManager;
        this.sessionRepository = sessionRepository;
        this.externalUserHandler = externalUserHandler;
        this.credentialsHandler = credentialsHandler;
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
                .flatMap(session -> Mono.justOrEmpty((Object) session.getAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME)))
                .map(SecurityContext.class::cast)
                .map(context -> getAuthenticatedUser(context.getAuthentication()))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuthenticatedUser> findAll(String userId) {
        return findAll()
                .filter(authenticatedUser -> Objects.equals(userId, authenticatedUser.getUserId()));
    }

    @Override
    @Transactional
    public Mono<ExternalAuthenticatedUser> createAuthentication(OAuthExternalUser externalUser) {
        return externalUserHandler
                .map(externalUser)
                .flatMap(userManager::createOrUpdate)
                .flatMap(externalUserEntity ->
                        repositoryManager
                                .findAll()
                                .flatMap(repository -> loadCredentials(externalUser, repository))
                                .collectList()
                                .map(credentials ->
                                        new ExternalAuthenticatedUser(
                                                UUID.randomUUID().toString(),
                                                externalUserEntity.getId(),
                                                externalUser.getToken(),
                                                externalUserEntity.getRoles(),
                                                credentials
                                        )
                                )

                );
    }

    @Override
    public Mono<Authentication> createAuthentication(InternalUserDetails principal) {
        return Mono.just(
                new UsernamePasswordAuthenticationToken(
                        new InternalAuthenticatedUser(UUID.randomUUID().toString(), principal.getId(), principal.getRoles()),
                        principal.getPassword(),
                        principal.getAuthorities()
                )
        );
    }

    @Override
    public Mono<Void> updateAuthentications(UserEntity user) {
        return this
                .findAll(user.getId())
                .map(authenticatedUser -> authenticatedUser.updateSessionRoles(
                        Stream
                                .concat(
                                        authenticatedUser.getSessionRoles().stream().filter(role -> !role.isAssignableByEndUser()),
                                        user.getRoles().stream()
                                )
                                .collect(toList())
                ))
                .flatMap(this::update)
                .then();
    }

    @Override
    public Mono<Void> deleteAllAuthentications(String userId) {
        return this
                .findAll(userId)
                .flatMap(this::delete)
                .then();
    }

    /**
     * Loads the {@link RepositoryCredentials credentails} to use for the specified {@link ExternalUserEntity external user}
     * and the specified {@link RepositoryEntity repository}.
     */
    private Mono<RepositoryCredentials> loadCredentials(OAuthExternalUser externalUser, RepositoryEntity repository) {
        return credentialsHandler.loadCredentials(
                externalUser.getOauthClient(),
                externalUser.getToken(),
                repository
        );
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
