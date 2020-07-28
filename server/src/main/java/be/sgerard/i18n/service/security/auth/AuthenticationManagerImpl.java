package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.external.OAuthUserMapper;
import be.sgerard.i18n.service.security.auth.external.OAuthUserRepositoryCredentialsHandler;
import be.sgerard.i18n.service.security.session.repository.SessionRepository;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.context.annotation.Lazy;
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

import static be.sgerard.i18n.model.event.EventType.DELETED_AUTHENTICATED_USER;
import static be.sgerard.i18n.model.event.EventType.UPDATED_AUTHENTICATED_USER;
import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.getAuthenticatedUser;
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
    private final OAuthUserRepositoryCredentialsHandler credentialsHandler;
    private final EventService eventService;

    @Lazy
    public AuthenticationManagerImpl(UserManager userManager,
                                     RepositoryManager repositoryManager,
                                     SessionRepository sessionRepository,
                                     OAuthUserMapper externalUserHandler,
                                     OAuthUserRepositoryCredentialsHandler credentialsHandler,
                                     EventService eventService) {
        this.userManager = userManager;
        this.repositoryManager = repositoryManager;
        this.sessionRepository = sessionRepository;
        this.externalUserHandler = externalUserHandler;
        this.credentialsHandler = credentialsHandler;
        this.eventService = eventService;
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
                .filter(authenticatedUser -> Objects.equals(userId, authenticatedUser.getUser().getId()));
    }

    @Override
    @Transactional
    public Mono<AuthenticatedUser> update(AuthenticatedUser authenticatedUser) {
        final AuthenticatedUserDto dto = AuthenticatedUserDto.builder(authenticatedUser).build();

        return findSession(authenticatedUser)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.authenticatedUserNotFoundException(authenticatedUser.getId())))
                .flatMap(webSession -> sessionRepository.deleteById(webSession.getId()))
                .then(
                        Mono
                                .zip(
                                        eventService.sendEventToUser(authenticatedUser, UPDATED_AUTHENTICATED_USER, dto),
                                        eventService.sendEventToUsers(UserRole.ADMIN, UPDATED_AUTHENTICATED_USER, dto)
                                )
                                .then(Mono.just(authenticatedUser))
                );
    }

    @Override
    @Transactional
    public Mono<AuthenticatedUser> delete(AuthenticatedUser authenticatedUser) {
        final AuthenticatedUserDto dto = AuthenticatedUserDto.builder(authenticatedUser).build();

        return findSession(authenticatedUser)
                .switchIfEmpty(Mono.empty())
                .then(
                        eventService
                                .sendEventToUsers(UserRole.ADMIN, DELETED_AUTHENTICATED_USER, dto)
                                .then(Mono.just(authenticatedUser))
                );
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
                                                UserDto.builder(externalUserEntity).build(),
                                                externalUser.getToken(),
                                                externalUserEntity.getRoles(),
                                                credentials
                                        )
                                )

                );
    }

    @Override
    @Transactional
    public Mono<InternalAuthenticatedUser> createAuthentication(String username) {
        return userManager
                .finUserByNameOrDie(username)
                .flatMap(user ->
                        repositoryManager
                                .findAll()
                                .collectList()
                                .map(repositories ->
                                        new InternalAuthenticatedUser(
                                                UUID.randomUUID().toString(),
                                                UserDto.builder(user).build(),
                                                user.getPassword(),
                                                user.getRoles()
                                        )
                                )
                );

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
                                .justOrEmpty((Object) session.getAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME))
                                .map(SecurityContext.class::cast)
                                .map(context -> getAuthenticatedUser(context.getAuthentication()))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .filter(user -> Objects.equals(authenticatedUser.getId(), user.getId()))
                                .thenReturn(session)
                )
                .next();
    }

}
