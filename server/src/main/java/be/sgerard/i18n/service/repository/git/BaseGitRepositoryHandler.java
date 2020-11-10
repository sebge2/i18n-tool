package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.model.repository.dto.BaseGitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
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
public abstract class BaseGitRepositoryHandler<E extends BaseGitRepositoryEntity, C extends RepositoryCreationDto, P extends BaseGitRepositoryPatchDto, D extends RepositoryCredentials>
        implements RepositoryHandler<E, C, P, D> {

    private final GitRepositoryApiProvider apiProvider;

    protected BaseGitRepositoryHandler(GitRepositoryApiProvider apiProvider) {
        this.apiProvider = apiProvider;
    }

    @Override
    public Mono<E> initializeRepository(E repository, D credentials) {
        try {
            return initApi(repository, credentials)
                    .map(GitRepositoryApi.class::cast)
                    .flatMap(api ->
                            Mono
                                    .just(api)
                                    .map(GitRepositoryApi::init)
                                    .thenReturn(repository)
                                    .doFinally(signalType -> api.close())
                    );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<E> deleteRepository(E repository, D credentials) {
        try {
            return initApi(repository, credentials)
                    .map(GitRepositoryApi.class::cast)
                    .flatMap(api ->
                            Mono
                                    .just(api)
                                    .map(GitRepositoryApi::delete)
                                    .thenReturn(repository)
                                    .doFinally(signalType -> api.close())
                    );
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
     * Returns the {@link GitRepositoryApi Git API} to use for the specified configuration.
     */
    protected Mono<GitRepositoryApi> initApi(GitRepositoryApi.Configuration configuration) {
        return Mono.just(apiProvider.initApi(configuration));
    }
}
