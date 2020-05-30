package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.service.repository.RepositoryApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Abstract {@link RepositoryHandler repository handler} for Git.
 *
 * @author Sebastien Gerard
 */
@Component
public abstract class BaseGitRepositoryHandler<E extends BaseGitRepositoryEntity, C extends RepositoryCreationDto, P extends GitRepositoryPatchDto> implements RepositoryHandler<E, C, P> {

    /**
     * Default master branch.
     */
    public static final String DEFAULT_BRANCH = "master";

    protected BaseGitRepositoryHandler() {
    }

    /**
     * Initializes the {@link DefaultGitRepositoryApi.Configuration configuration} to use to access Git.
     */
    protected abstract DefaultGitRepositoryApi.Configuration createConfiguration(E repository);

    @Override
    public Mono<E> initializeRepository(E repository) {
        try {
            initApiFromEntity(repository).init();

            return Mono.just(repository);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<E> deleteRepository(E repository) throws RepositoryException {
        try {
            initApiFromEntity(repository).delete();

            return Mono.just(repository);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<RepositoryApi> createAPI(E repository) throws RepositoryException {
        return Mono.just(initApiFromEntity(repository));
    }

    /**
     * Validates the specified repository.
     */
    protected Mono<E> validateRepository(E repository) {
        try {
            initApiFromEntity(repository).validateInfo();

            return Mono.just(repository);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    /**
     * Updates the repository based on the specified patch.
     */
    protected void updateFromPatch(P patchDto, E repository) {
        repository.setDefaultBranch(patchDto.getDefaultBranch().orElse(repository.getDefaultBranch()));
    }

    /**
     * Returns the {@link GitRepositoryApi Git API} to use for the specified repository.
     */
    protected GitRepositoryApi initApiFromEntity(E repository) {
        return DefaultGitRepositoryApi
                .createAPI(repository, createConfiguration(repository));
    }
}
