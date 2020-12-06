package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.repository.CurrentUser;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import reactor.core.publisher.Mono;

/**
 * Provides {@link RepositoryCredentials credentials} for a particular user and repository.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryCredentialsHandler<C extends RepositoryCredentials, R extends RepositoryEntity> {

    /**
     * Returns whether the handler supports the specified {@link RepositoryEntity repository}.
     */
    boolean support(RepositoryEntity repository);

    /**
     * Loads the {@link RepositoryCredentials credentails} to access the specified repository when there is no current {@link CurrentUser user}.
     */
    Mono<C> loadStaticCredentials(R repository);

    /**
     * Loads the {@link RepositoryCredentials credentails} to access the specified repository for the current {@link CurrentUser user}.
     */
    Mono<C> loadUserCredentials(R repository, CurrentUser currentUser);

}
