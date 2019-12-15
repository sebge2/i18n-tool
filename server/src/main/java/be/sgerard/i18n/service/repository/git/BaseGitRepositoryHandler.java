package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.service.repository.RepositoryApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Abstract {@link RepositoryHandler repository handler} for Git.
 *
 * @author Sebastien Gerard
 */
@Component
public abstract class BaseGitRepositoryHandler<E extends GitRepositoryEntity, C extends RepositoryCreationDto, P extends GitRepositoryPatchDto> implements RepositoryHandler<E, C, P> {

    /**
     * Default master branch.
     */
    public static final String DEFAULT_BRANCH = "master";

    protected BaseGitRepositoryHandler() {
    }

    /**
     * Initializes the {@link DefaultGitAPI.Configuration configuration} to use to access Git.
     */
    protected abstract DefaultGitAPI.Configuration createConfiguration(E repository);

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
        return Mono.just(new RepositoryApiAdapter(repository, initApiFromEntity(repository)));
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
     * Returns the {@link GitAPI Git API} to use for the specified repository.
     */
    protected GitAPI initApiFromEntity(E repository) {
        return DefaultGitAPI
                .createAPI(createConfiguration(repository));
    }

    /**
     * {@link RepositoryApi Repository API} adapting the {@link GitAPI Git API}.
     */
    public static final class RepositoryApiAdapter implements RepositoryApi {

        private final GitRepositoryEntity repository;
        private final GitAPI gitAPI;

        public RepositoryApiAdapter(GitRepositoryEntity repository, GitAPI gitAPI) {
            this.repository = repository;
            this.gitAPI = gitAPI;
        }

        @Override
        public Flux<String> listBranches() throws RepositoryException {
            return null;
//            return gitAPI
//                    .update()
//                    .listRemoteBranches()
//                    .stream()
//                    .filter(this::canBeAssociatedToWorkspace)
//                    .collect(toList());
        }

        @Override
        public Flux<File> listAllFiles(String branch, File file) throws IOException {
            return null;
        }

        @Override
        public Flux<File> listNormalFiles(String branch, File file) throws IOException {
            return null;
        }

        @Override
        public Flux<File> listDirectories(String branch, File file) throws IOException {
            return null;
        }

        private boolean canBeAssociatedToWorkspace(String name) {
            return repository.getAllowedBranchesPattern().matcher(name).matches();
        }
    }

}
