package be.sgerard.i18n.service.repository.github.webhook;

import be.sgerard.i18n.model.repository.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.model.repository.github.external.GitHubPullRequestEventDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.workspace.persistence.GitHubReviewEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

/**
 * {@link GitHubWebHookEventHandler Event handler} for the {@link GitHubPullRequestEventDto pull-request event}.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubPullRequestEventHandler implements GitHubWebHookEventHandler<GitHubPullRequestEventDto> {

    private static final Logger logger = LoggerFactory.getLogger(GitHubPullRequestEventHandler.class);

    private final WorkspaceManager workspaceManager;

    public GitHubPullRequestEventHandler(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }

    @Override
    public boolean support(BaseGitHubWebHookEventDto event) {
        return event instanceof GitHubPullRequestEventDto;
    }

    @Override
    public Mono<WorkspaceEntity> handle(GitHubRepositoryEntity repository, GitHubPullRequestEventDto event) {
        if (!event.getPullRequest().getStatus().isFinished()) {
            logger.info("The pull request {} is not finished, but is {}, nothing will be performed.", event.getPullRequest().getNumber(), event.getPullRequest().getStatus());
            return Mono.empty();
        }

        return workspaceManager
                .findAll(repository.getId())
                .filter(workspace -> workspace.getReview(GitHubReviewEntity.class).isPresent())
                .filter(workspace -> workspace.getReviewOrDie(GitHubReviewEntity.class).getPullRequestNumber() == event.getPullRequest().getNumber())
                .last()
                .onErrorResume(NoSuchElementException.class, e -> Mono.empty())
                .flatMap(workspace -> workspaceManager.finishReview(workspace.getId()));
    }
}
