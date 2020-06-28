package be.sgerard.i18n.service.workspace.strategy.git;

import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.workspace.strategy.WorkspaceTranslationsStrategy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link WorkspaceTranslationsStrategy Strategy} for {@link GitRepositoryEntity Git repositories}. Changes are directly
 * published to the original branch.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitDirectPushWorkspaceTranslationsStrategy extends BaseGitWorkspaceTranslationsStrategy {

    public GitDirectPushWorkspaceTranslationsStrategy(RepositoryManager repositoryManager, TranslationManager translationManager) {
        super(repositoryManager, translationManager);
    }

    @Override
    public Mono<Boolean> isReviewFinished(WorkspaceEntity workspace) {
        return Mono.error(new UnsupportedOperationException("This strategy does not support review. Make sure that strategies have been properly registered."));
    }

    @Override
    public Mono<WorkspaceEntity> onPublish(WorkspaceEntity workspace, String message) {
        return repositoryManager
                .applyOnRepository(
                        workspace.getRepository(),
                        GitRepositoryApi.class,
                        api -> translationManager
                                .writeTranslations(workspace, new GitTranslationRepositoryWriteApi(api, workspace.getBranch(), workspace.getBranch()))
                                .doOnSuccess(v -> api.commitAll(message).push())
                                .thenReturn(workspace)
                )
                .flatMap(m -> m);
    }

    @Override
    public Mono<WorkspaceEntity> onDelete(WorkspaceEntity workspace) {
        return Mono.just(workspace);
    }

    @Override
    protected boolean doSupport(BaseGitRepositoryEntity repository) {
        return repository instanceof GitRepositoryEntity;
    }

}
