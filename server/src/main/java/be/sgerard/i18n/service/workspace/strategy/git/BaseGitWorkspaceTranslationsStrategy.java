package be.sgerard.i18n.service.workspace.strategy.git;

import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.workspace.strategy.WorkspaceTranslationsStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Base implementation for {@link WorkspaceTranslationsStrategy strategies} using {@link GitRepositoryEntity Git repositories}
 *
 * @author Sebastien Gerard
 */
public abstract class BaseGitWorkspaceTranslationsStrategy implements WorkspaceTranslationsStrategy {

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
                .applyOnRepository(
                        repository.getId(),
                        GitRepositoryApi.class,
                        api -> Flux.fromStream(
                                api
                                        .pull()
                                        .listRemoteBranches()
                                        .stream()
                                        .filter(branch -> isAllowedBranch(repository, branch))
                        )
                )
                .flatMapMany(flux -> flux);
    }

    @Override
    public Mono<WorkspaceEntity> onInitialize(WorkspaceEntity workspace) {
        return repositoryManager
                .applyOnRepository(
                        workspace.getRepository().getId(),
                        GitRepositoryApi.class,
                        api -> {
                            translationManager.readTranslations(workspace, new GitTranslationRepositoryReadApi(api, workspace.getBranch()));

                            return workspace;
                        }
                );
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
}
