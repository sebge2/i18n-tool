package be.sgerard.i18n.service.workspace.strategy.github;

import be.sgerard.i18n.model.github.GitHubPullRequestDto;
import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.workspace.persistence.GitHubReviewEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.client.GitHubClient;
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

    private final GitHubClient gitHubClient;

    public GitHubPRWorkspaceTranslationsStrategy(RepositoryManager repositoryManager,
                                                 TranslationManager translationManager,
                                                 GitHubClient gitHubClient) {
        super(repositoryManager, translationManager);
        this.gitHubClient = gitHubClient;
    }

    @Override
    public Mono<Boolean> isReviewFinished(WorkspaceEntity workspace) {
        return gitHubClient
                .findByNumber(workspace.getRepository(), workspace.getReviewOrDie(GitHubReviewEntity.class).getPullRequestNumber())
                .map(GitHubPullRequestDto::getStatus)
                .map(GitHubPullRequestStatus::isFinished)
                .switchIfEmpty(Mono.just(true));
    }

    @Override
    public Mono<WorkspaceEntity> onPublish(WorkspaceEntity workspace, String message) {
        return repositoryManager
                .applyOnRepository(
                        workspace.getRepository(),
                        GitRepositoryApi.class,
                        api ->
                                Mono
                                        .just(generatePullRequestBranchName(workspace, api))
                                        .flatMap(prBranch ->
                                                translationManager
                                                        .writeTranslations(workspace, new GitTranslationRepositoryWriteApi(api, workspace.getBranch(), prBranch))
                                                        .doOnSuccess(v -> api.commitAll(message).push())
                                                        .then(Mono.defer(() ->
                                                                gitHubClient
                                                                        .createRequest(workspace.getRepository(), message, prBranch, workspace.getBranch())
                                                                        .map(pr -> workspace.setReview(new GitHubReviewEntity(prBranch, pr.getNumber()))))
                                                        )
                                        )
                )
                .flatMap(m -> m);
    }

    @Override
    public Mono<WorkspaceEntity> onDelete(WorkspaceEntity workspace) {
        return repositoryManager
                .applyOnRepository(
                        workspace.getRepository(),
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

    private String generatePullRequestBranchName(WorkspaceEntity workspace, GitRepositoryApi api) {
        return generateUniqueBranch(workspace.getBranch() + "_i18n_" + LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault()).toString(), api);
    }
}
