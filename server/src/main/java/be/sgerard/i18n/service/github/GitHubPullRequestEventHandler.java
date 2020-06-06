package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.service.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.service.github.external.GitHubPullRequestEventDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link GitHubWebHookEventHandler Event handler} for the {@link GitHubPullRequestEventDto pull-request event}.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubPullRequestEventHandler implements GitHubWebHookEventHandler<GitHubPullRequestEventDto> {

    private final GitHubWebHookCallback callback;

    public GitHubPullRequestEventHandler(GitHubWebHookCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean support(BaseGitHubWebHookEventDto event) {
        return event instanceof GitHubPullRequestEventDto;
    }

    @Override
    public Mono<Void> handle(GitHubRepositoryEntity repository, GitHubPullRequestEventDto event) {
        return callback.onPullRequestUpdate(repository, event.getPullRequest().getNumber(), event.getPullRequest().getStatus());
    }
}
