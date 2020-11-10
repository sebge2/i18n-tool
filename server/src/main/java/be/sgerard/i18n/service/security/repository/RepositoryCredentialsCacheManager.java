package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import be.sgerard.i18n.model.security.repository.RepositoryCredentialsSet;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.repository.listener.RepositoryListener;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.security.auth.context.AuthenticationUserContextProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

/**
 * Manager of the cache containing repository credentials. This cache is stored in the authenticated users.
 *
 * @author Sebastien Gerard
 */
@Component
class RepositoryCredentialsCacheManager implements RepositoryListener<RepositoryEntity>, AuthenticationUserContextProcessor {

    /**
     * Key in the authenticated user's context.
     */
    public static final String CONTEXT_KEY = "REPOSITORY_CREDENTIALS";

    private final AuthenticationUserManager authenticationUserManager;
    private final RepositoryCredentialsManager credentialsManager;
    private final RepositoryManager repositoryManager;

    @Lazy
    public RepositoryCredentialsCacheManager(AuthenticationUserManager authenticationUserManager,
                                             RepositoryCredentialsManager credentialsManager,
                                             RepositoryManager repositoryManager) {
        this.authenticationUserManager = authenticationUserManager;
        this.credentialsManager = credentialsManager;
        this.repositoryManager = repositoryManager;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<Void> afterCreate(RepositoryEntity repository) {
        return reload(repository);
    }

    @Override
    public Mono<Void> afterUpdate(RepositoryPatchDto patch, RepositoryEntity repository) {
        return reload(repository);
    }

    @Override
    public Mono<Void> beforeDelete(RepositoryEntity repository) {
        return deleteAll(repository);
    }

    @Override
    public Mono<Map<String, Object>> onCreate(AuthenticatedUser authenticatedUser) {
        return this
                .repositoryManager
                .findAll()
                .flatMap(repository -> credentialsManager.loadUserCredentials(repository, authenticatedUser))
                .collectList()
                .map(credentials -> Collections.singletonMap(CONTEXT_KEY, new RepositoryCredentialsSet(credentials)));
    }

    /**
     * Returns credentials that are eventually cached in the specified repository (may return empty).
     */
    public Mono<RepositoryCredentials> getCredentials(RepositoryEntity repository, AuthenticatedUser authenticatedUser) {
        return Mono
                .justOrEmpty(authenticatedUser
                        .getContextValue(CONTEXT_KEY, RepositoryCredentialsSet.class)
                        .flatMap(set -> set.getCredentials(repository.getId(), RepositoryCredentials.class))
                );
    }

    /**
     * Updates repository credentials of all the {@link AuthenticatedUser users}.
     *
     * @see AuthenticatedUser#getContext()
     */
    private Mono<Void> reload(RepositoryEntity repository) {
        return this
                .authenticationUserManager
                .findAll()
                .flatMap(authenticatedUser ->
                        credentialsManager
                                .loadUserCredentialsOrDie(repository)
                                .map(credentials ->
                                        authenticatedUser.updateContext(
                                                CONTEXT_KEY,
                                                authenticatedUser
                                                        .getContextValue(CONTEXT_KEY, RepositoryCredentialsSet.class)
                                                        .orElse(new RepositoryCredentialsSet())
                                                        .update(credentials)
                                        )
                                )
                                .thenReturn(authenticatedUser)
                )
                .flatMap(authenticationUserManager::update)
                .then();
    }

    /**
     * Deletes repository credentials of all the {@link AuthenticatedUser users}.
     *
     * @see AuthenticatedUser#getContext()
     */
    private Mono<Void> deleteAll(RepositoryEntity repository) {
        return this
                .authenticationUserManager
                .findAll()
                .map(authenticatedUser ->
                        authenticatedUser.updateContext(
                                CONTEXT_KEY,
                                authenticatedUser
                                        .getContextValue(CONTEXT_KEY, RepositoryCredentialsSet.class)
                                        .orElse(new RepositoryCredentialsSet())
                                        .remove(repository.getId())
                        )
                )
                .flatMap(authenticationUserManager::update)
                .then();
    }
}
