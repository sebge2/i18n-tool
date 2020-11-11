package be.sgerard.i18n.service.workspace.strategy.github;

import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import be.sgerard.i18n.model.repository.github.external.GitHubPullRequestStatus;
import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.workspace.persistence.GitHubReviewEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.repository.github.GitHubService;
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
import java.util.Optional;

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

    private final GitHubService gitHubService;

    public GitHubPRWorkspaceTranslationsStrategy(RepositoryManager repositoryManager,
                                                 TranslationManager translationManager,
                                                 GitHubService gitHubService) {
        super(repositoryManager, translationManager);
        this.gitHubService = gitHubService;
    }

    @Override
    public Mono<Boolean> isReviewFinished(WorkspaceEntity workspace, RepositoryEntity repository) {
        return gitHubService
                .findByNumber(workspace.getRepository(), workspace.getReviewOrDie(GitHubReviewEntity.class).getPullRequestNumber())
                .map(GitHubPullRequestDto::getStatus)
                .map(GitHubPullRequestStatus::isFinished)
                .switchIfEmpty(Mono.just(true));
    }

    @Override
    public Mono<WorkspaceEntity> onPublish(WorkspaceEntity workspace, String message) {
        return repositoryManager
                .applyGetMono(
                        workspace.getRepository(),
                        GitRepositoryApi.class,
                        api ->
                                Mono
                                        .defer(() -> Mono.just(generatePullRequestBranchName(workspace, api)))
                                        .flatMap(prBranch ->
                                                translationManager
                                                        .writeTranslations(workspace, new GitTranslationRepositoryWriteApi(api, workspace.getBranch(), prBranch))
                                                        .then()
                                                        .doOnSuccess(v -> api.commitAll(message).push())
                                                        .then(Mono.defer(() ->
                                                                gitHubService
                                                                        .createRequest(workspace.getRepository(), message, prBranch, workspace.getBranch())
                                                                        .map(pr -> workspace.setReview(new GitHubReviewEntity(prBranch, pr.getNumber()))))
                                                        )
                                        )
                );
    }

    @Override
    public Mono<WorkspaceEntity> onDelete(WorkspaceEntity workspace) {
        return repositoryManager
                .applyGetMono(
                        workspace.getRepository(),
                        GitRepositoryApi.class,
                        api -> {
                            final Optional<GitHubReviewEntity> gitHubReview = workspace
                                    .getReview(GitHubReviewEntity.class);

                            if (gitHubReview.isPresent()) {
                                try {
                                    api.removeBranch(gitHubReview.get().getPullRequestBranch());

                                    logger.info("The branch {} has been removed.", gitHubReview.get().getPullRequestBranch());
                                } catch (RepositoryException e) {
                                    logger.error(String.format("Error while removing the branch %s.", gitHubReview.get().getPullRequestBranch()), e);
                                }
                            } else {
                                logger.info("There is no review associated to the workspace {} skip this step.", workspace.getId());
                            }

                            return Mono.just(workspace);
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
