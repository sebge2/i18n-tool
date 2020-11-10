package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.repository.CurrentUser;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Implementation of the {@link RepositoryCredentialsManager credentials manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class RepositoryCredentialsManagerImpl implements RepositoryCredentialsManager {

    private final List<RepositoryCredentialsHandler<RepositoryCredentials, RepositoryEntity>> handlers;
    private final AuthenticationUserManager authenticationUserManager;
    private final RepositoryCredentialsCacheManager cacheManager;

    @Lazy
    @SuppressWarnings("unchecked")
    public RepositoryCredentialsManagerImpl(List<RepositoryCredentialsHandler<?, ?>> handlers,
                                            AuthenticationUserManager authenticationUserManager,
                                            RepositoryCredentialsCacheManager cacheManager) {
        this.handlers = (List<RepositoryCredentialsHandler<RepositoryCredentials, RepositoryEntity>>) (List<?>) handlers;
        this.authenticationUserManager = authenticationUserManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public Mono<RepositoryCredentials> loadStaticCredentials(RepositoryEntity repository) {
        return Flux
                .fromIterable(handlers)
                .filter(handler -> handler.support(repository))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported internal user and repository " + repository.getType() + ".")))
                .flatMap(handler -> handler.loadStaticCredentials(repository));
    }

    @Override
    public Mono<RepositoryCredentials> loadUserCredentialsOrDie(RepositoryEntity repository) {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .flatMap(authenticatedUser -> loadUserCredentials(repository, authenticatedUser));
    }

    @Override
    public Mono<RepositoryCredentials> loadUserCredentials(RepositoryEntity repository) {
        return authenticationUserManager
                .getCurrentUser()
                .flatMap(authenticatedUser -> loadUserCredentials(repository, authenticatedUser))
                .switchIfEmpty(Mono.defer(() -> this.loadStaticCredentials(repository)));
    }

    @Override
    public Mono<RepositoryCredentials> loadUserCredentials(RepositoryEntity repository, AuthenticatedUser authenticatedUser) {
        return this.cacheManager
                .getCredentials(repository, authenticatedUser)
                .switchIfEmpty(Mono.defer(() -> loadUserCredentialsNotCached(repository, authenticatedUser)));
    }

    /**
     * Maps the specified {@link AuthenticatedUser authenticated user} to the current user used for the remote repository.
     */
    private CurrentUser mapToCurrentUser(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser instanceof InternalAuthenticatedUser) {
            return new CurrentUser(authenticatedUser.getDisplayName(), authenticatedUser.getEmail(), null);
        } else if (authenticatedUser instanceof ExternalAuthenticatedUser) {
            return new CurrentUser(authenticatedUser.getDisplayName(), authenticatedUser.getEmail(), ((ExternalAuthenticatedUser) authenticatedUser).getToken());
        } else {
            throw new UnsupportedOperationException("Unsupported user [" + authenticatedUser + "].");
        }
    }

    /**
     * Loads credentials of the specified authenticated user. Those credentials are not present in the current authenticated user.
     */
    private Mono<RepositoryCredentials> loadUserCredentialsNotCached(RepositoryEntity repository, AuthenticatedUser authenticatedUser) {
        final CurrentUser currentUser = mapToCurrentUser(authenticatedUser);

        return Flux
                .fromIterable(handlers)
                .filter(handler -> handler.support(repository))
                .next()
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("Unsupported internal user and repository " + repository.getType() + ".")))
                .flatMap(handler -> handler.loadUserCredentials(repository, currentUser))
                .switchIfEmpty(Mono.error(() ->
                        new IllegalStateException("No credentials have been returned for the repository [" + repository + "]. " +
                                "Hint: check the credentials handler.")
                ));
    }
}
