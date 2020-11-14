package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.service.repository.listener.RepositoryListener;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link RepositoryListener Listener} refreshing credentials when a repository is updated.
 *
 * @author Sebastien Gerard
 */
@Component
public class UpdateCredentialsRepositoryListener implements RepositoryListener<RepositoryEntity> {

    private final AuthenticationUserManager authenticationUserManager;

    public UpdateCredentialsRepositoryListener(AuthenticationUserManager authenticationUserManager) {
        this.authenticationUserManager = authenticationUserManager;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<Void> afterCreate(RepositoryEntity repository) {
        return authenticationUserManager.updateAllRepositoryCredentials(repository.getId());
    }

    @Override
    public Mono<Void> afterUpdate(RepositoryPatchDto patch, RepositoryEntity repository) {
        if (patch instanceof GitHubRepositoryPatchDto && ((GitHubRepositoryPatchDto) patch).getAccessKey().isPresent()) {
            return authenticationUserManager.updateAllRepositoryCredentials(repository.getId());
        }

        return Mono.empty();
    }

    @Override
    public Mono<Void> beforeDelete(RepositoryEntity repository) {
        return authenticationUserManager.deleteAllRepositoryCredentials(repository.getId());
    }
}
