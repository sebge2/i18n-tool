package be.sgerard.i18n.service.workspace.strategy.git;

import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import be.sgerard.i18n.service.workspace.strategy.WorkspaceTranslationsStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Base implementation for {@link WorkspaceTranslationsStrategy strategies} using {@link GitRepositoryEntity Git repositories}
 *
 * @author Sebastien Gerard
 */
public abstract class BaseGitWorkspaceTranslationsStrategy implements WorkspaceTranslationsStrategy {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final RepositoryManager repositoryManager;
    protected final TranslationManager translationManager;

    public BaseGitWorkspaceTranslationsStrategy(RepositoryManager repositoryManager, TranslationManager translationManager) {
        this.repositoryManager = repositoryManager;
        this.translationManager = translationManager;
    }

    @Override
    public final boolean support(RepositoryEntity repository) {
        return repository instanceof BaseGitRepositoryEntity && doSupport((BaseGitRepositoryEntity) repository);
    }

    @Override
    public Flux<String> listBranches(RepositoryEntity repository) throws RepositoryException {
        return repositoryManager
                .applyGetFlux(
                        repository.getId(),
                        GitRepositoryApi.class,
                        api -> Flux.fromStream(
                                api
                                        .resetHardHead()
                                        .fetch()
                                        .listRemoteBranches()
                                        .stream()
                                        .filter(branch -> isAllowedBranch(repository, branch))
                        )
                );
    }

    @Override
    public boolean initializeOnCreate(WorkspaceEntity workspace, RepositoryEntity repository) {
        return Objects.equals(workspace.getBranch(), ((BaseGitRepositoryEntity) repository).getDefaultBranch());
    }

    @Override
    public Mono<WorkspaceEntity> onInitialize(WorkspaceEntity workspace) {
        return readTranslations(workspace);
    }

    @Override
    public Mono<WorkspaceEntity> onSynchronize(WorkspaceEntity workspace) throws WorkspaceException, RepositoryException {
        return readTranslations(workspace);
    }

    @Override
    public Mono<WorkspaceEntity> onDelete(WorkspaceEntity workspace) throws WorkspaceException, RepositoryException {
        return Flux
                .concat(
                        doOnDelete(workspace),
                        translationManager.deleteByWorkspace(workspace),
                        deleteBranch(workspace)
                )
                .then(Mono.just(workspace));
    }

    /**
     * Performs additional support check.
     */
    protected abstract boolean doSupport(BaseGitRepositoryEntity repository);

    /**
     * Returns whether the specified branch can be exposed.
     */
    private boolean isAllowedBranch(RepositoryEntity repository, String branch) {
        return ((BaseGitRepositoryEntity) repository).getAllowedBranches().matcher(branch).matches();
    }

    /**
     * Reads all translations from the repository and creates translation entities.
     */
    private Mono<WorkspaceEntity> readTranslations(WorkspaceEntity workspace) {
        return repositoryManager
                .applyGetMono(
                        workspace.getRepository(),
                        GitRepositoryApi.class,
                        api ->
                                translationManager
                                        .readTranslations(workspace, new GitTranslationRepositoryReadApi(api, workspace.getBranch()))
                                        .then(Mono.just(workspace))
                );
    }

    /**
     * Performs additional actions when deleting the specified workspace.
     */
    protected Mono<Void> doOnDelete(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Deletes the branch associated to the specified workspace.
     */
    private Mono<Void> deleteBranch(WorkspaceEntity workspace) {
        return repositoryManager
                .applyGetMono(
                        workspace.getRepository(),
                        GitRepositoryApi.class,
                        api -> {
                            try {
                                api.removeBranch(workspace.getBranch());

                                logger.info("The branch {} has been removed.", workspace.getBranch());
                            } catch (RepositoryException e) {
                                logger.error(String.format("Error while removing the branch %s.", workspace.getBranch()), e);
                            }

                            return Mono.empty();
                        }
                );
    }

}
