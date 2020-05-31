package be.sgerard.i18n.service.workspace.strategy.github;

import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.workspace.GitHubReviewEntity;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.workspace.strategy.WorkspaceTranslationsStrategy;
import be.sgerard.i18n.service.workspace.strategy.git.BaseGitWorkspaceTranslationsStrategy;
import be.sgerard.i18n.service.workspace.strategy.git.GitTranslationRepositoryWriteApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static be.sgerard.i18n.service.workspace.strategy.git.GitTranslationRepositoryWriteApi.generateUniqueBranch;

/**
 * {@link WorkspaceTranslationsStrategy Strategy} for {@link GitHubRepositoryEntity GitHub repositories}. When pushing
 * changes, a pull request will be created.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubPRWorkspaceTranslationsStrategy extends BaseGitWorkspaceTranslationsStrategy {

    private static final Logger logger = LoggerFactory.getLogger(GitHubPRWorkspaceTranslationsStrategy.class);

    public GitHubPRWorkspaceTranslationsStrategy(RepositoryManager repositoryManager, TranslationManager translationManager) {
        super(repositoryManager, translationManager);
    }

    @Override
    public Mono<Boolean> isReviewFinished(WorkspaceEntity workspace) {
        return Mono.just(false); // TODO
    }

    @Override
    public Mono<WorkspaceEntity> onPublish(WorkspaceEntity workspace) {
        return repositoryManager
                .applyOnRepository(
                        workspace.getRepository().getId(),
                        GitRepositoryApi.class,
                        api -> {
                            final String pullRequestBranch = generateUniqueBranch(
                                    workspace.getBranch() + "_i18n_" + LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault()).toString(),
                                    api
                            );

                            translationManager.writeTranslations(workspace, new GitTranslationRepositoryWriteApi(api, workspace.getBranch(), pullRequestBranch));

                            workspace.setReview(new GitHubReviewEntity(workspace, pullRequestBranch));

//
//                final UserDto currentUser = credentialsProvider.getCurrentUserOrFail().getUser(); TODO
                            api.commitAll("" /*message*/, "", "").push();

//
//                final int requestNumber = pullRequestManager.createRequest(message, pullRequestBranch, workspaceEntity.getBranch());
//
//                workspaceEntity.setPullRequestNumber(requestNumber);

                            return workspace;
                        }
                );
    }

    @Override
    public Mono<WorkspaceEntity> onDelete(WorkspaceEntity workspace) {
        return repositoryManager
                .applyOnRepository(
                        workspace.getRepository().getId(),
                        GitRepositoryApi.class,
                        api -> {
                            workspace
                                    .getReview(GitHubReviewEntity.class)
                                    .ifPresent(review -> {
                                        api.removeBranch(review.getPullRequestBranch());

                                        logger.info("The branch {} has been removed.", review.getPullRequestBranch());
                                    });

                            return workspace;
                        }
                );
    }

    @Override
    protected boolean doSupport(BaseGitRepositoryEntity repository) {
        return repository instanceof GitHubRepositoryEntity;
    }
}
