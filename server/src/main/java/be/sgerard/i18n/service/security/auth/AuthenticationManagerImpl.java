package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.security.auth.external.OAuthUserMapper;
import be.sgerard.i18n.service.security.auth.external.OAuthUserRepositoryCredentialsHandler;
import be.sgerard.i18n.service.user.UserManager;
import be.sgerard.i18n.service.user.listener.UserListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.getAuthenticatedUser;
import static java.util.stream.Collectors.toList;
import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

/**
 * Implementation of the {@link AuthenticationManager authentication manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationManagerImpl implements AuthenticationManager, UserListener {

    private final UserManager userManager;
    private final RepositoryManager repositoryManager;
    private final FindByIndexNameSessionRepository<Session> sessionRepository;
    private final OAuthUserMapper externalUserHandler;
    private final OAuthUserRepositoryCredentialsHandler credentialsHandler;

    @SuppressWarnings("unchecked")
    @Lazy
    public AuthenticationManagerImpl(UserManager userManager,
                                     RepositoryManager repositoryManager,
                                     FindByIndexNameSessionRepository<?> sessionRepository,
                                     OAuthUserMapper externalUserHandler,
                                     OAuthUserRepositoryCredentialsHandler credentialsHandler) {
        this.userManager = userManager;
        this.repositoryManager = repositoryManager;
        this.sessionRepository = (FindByIndexNameSessionRepository<Session>) sessionRepository;
        this.externalUserHandler = externalUserHandler;
        this.credentialsHandler = credentialsHandler;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ExternalAuthenticatedUser> createAuthentication(OAuthExternalUser externalUser) {
        return externalUserHandler
                .map(externalUser)
                .flatMap(userManager::createOrUpdateUser)
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
    @Transactional(readOnly = true)
    public Mono<InternalAuthenticatedUser> createAuthentication(String username) {
        return userManager
                .finUserByNameOrDie(username)
                .flatMap(user ->
                        repositoryManager
                                .findAll()
                                .flatMap(repository -> repositoryManager.getDefaultCredentials(repository.getId()))
                                .collectList()
                                .map(repositories ->
                                        new InternalAuthenticatedUser(
                                                UUID.randomUUID().toString(),
                                                UserDto.builder(user).build(),
                                                user.getPassword(),
                                                user.getRoles(),
                                                repositories
                                        )
                                )
                );

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthenticatedUser> getCurrentUser() {
        return getAuthenticatedUser(SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public Mono<Void> onUpdate(UserEntity user) {
        sessionRepository.findByPrincipalName(user.getId()).values().stream()
                .map(Session.class::cast)
                .filter(session -> session.getAttributeNames().contains(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME))
                .filter(session -> ((SecurityContext) session.getAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME)).getAuthentication().getPrincipal() instanceof AuthenticatedUser)
                .forEach(session -> update(session, user));

        return Mono.empty();
    }

    @Override
    public Mono<Void> onDelete(UserEntity user) {
        sessionRepository.findByPrincipalName(user.getId()).values().stream()
                .map(Session.class::cast)
                .forEach(session -> sessionRepository.deleteById(session.getId()));

        return Mono.empty();
    }

    /**
     * Loads the {@link RepositoryCredentials credentails} to use for the specified {@link ExternalUserEntity external user}
     * and the specified {@link RepositoryEntity repository}.
     */
    private Mono<RepositoryCredentials> loadCredentials(OAuthExternalUser externalUser, RepositoryEntity repository) {
        return credentialsHandler.loadCredentials(
                externalUser.getOauthClient(),
                externalUser.getToken(),
                repository,
                Mono.defer(() -> repositoryManager.getDefaultCredentials(repository.getId()))
        );
    }


    private void update(Session session, UserEntity updatedUser) {
        final SecurityContext securityContext = session.getAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME);
        final AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getAuthentication().getPrincipal();

//        final AuthenticatedUser updatedAuthenticatedUser = updateAuthenticatedUser(authenticatedUser, updatedUser);
//        final AuthenticatedUserDto updatedAuthenticatedUserDto = AuthenticatedUserDto.builder(updatedAuthenticatedUser).build();

        final SecurityContextImpl updatedSecurityContext = new SecurityContextImpl();
//        updatedSecurityContext.setAuthentication(updateAuthentication(securityContext.getAuthentication(), updatedAuthenticatedUser));
//        session.setAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, updatedSecurityContext);

        sessionRepository.save(session);

//            eventService.broadcastInternally(UPDATED_AUTHENTICATED_USER, updatedAuthenticatedUserDto);
//        eventService.sendEventToUsers(UserRole.ADMIN, UPDATED_AUTHENTICATED_USER, updatedAuthenticatedUserDto);
    }

    private AuthenticatedUser updateAuthenticatedUser(AuthenticatedUser authenticatedUser, UserDto updatedUser) {
        return authenticatedUser.updateSessionRoles(
                Stream
                        .concat(
                                authenticatedUser.getSessionRoles().stream().filter(role -> !role.isAssignableByEndUser()),
                                updatedUser.getRoles().stream()
                        )
                        .collect(toList())
        );
    }

}
