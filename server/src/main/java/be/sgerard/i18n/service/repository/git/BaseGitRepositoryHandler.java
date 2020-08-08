package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.service.repository.RepositoryApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

/**
 * Abstract {@link RepositoryHandler repository handler} for Git.
 *
 * @author Sebastien Gerard
 */
@Component
public abstract class BaseGitRepositoryHandler<E extends BaseGitRepositoryEntity, C extends RepositoryCreationDto, P extends GitRepositoryPatchDto> implements RepositoryHandler<E, C, P> {

    private final GitRepositoryApiProvider apiProvider;

    protected BaseGitRepositoryHandler(GitRepositoryApiProvider apiProvider) {
        this.apiProvider = apiProvider;
    }

    /**
     * Initializes the {@link DefaultGitRepositoryApi.Configuration configuration} to use to access Git.
     */
    protected abstract Mono<DefaultGitRepositoryApi.Configuration> createConfiguration(E repository);

    @Override
    public Mono<E> initializeRepository(E repository) {
        try {
            return initApiFromEntity(repository)
                    .map(api -> {
                        api.init();

                        return repository;
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<E> deleteRepository(E repository) throws RepositoryException {
        try {
            return initApiFromEntity(repository)
                    .map(api -> {
                        api.delete();

                        return repository;
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<RepositoryApi> createAPI(E repository) throws RepositoryException {
        return initApiFromEntity(repository).map(a -> a);
    }

    /**
     * Validates the specified repository.
     */
    protected Mono<E> validateRepository(E repository) {
        try {
            return initApiFromEntity(repository)
                    .map(api -> {
                        api.validateInfo();

                        return repository;
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    /**
     * Updates the repository based on the specified patch.
     */
    protected void updateFromPatch(P patchDto, E repository) {
        repository.setDefaultBranch(patchDto.getDefaultBranch().orElse(repository.getDefaultBranch()));
        repository.setAllowedBranches(patchDto.getAllowedBranches().map(Pattern::compile).orElse(repository.getAllowedBranches()));
    }

    /**
     * Returns the {@link GitRepositoryApi Git API} to use for the specified repository.
     */
    protected Mono<GitRepositoryApi> initApiFromEntity(E repository) {
        return createConfiguration(repository).map(apiProvider::createApi);
    }
}
