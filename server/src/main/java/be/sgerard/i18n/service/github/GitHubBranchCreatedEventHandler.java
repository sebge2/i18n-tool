package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.service.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.service.github.external.GitHubBranchCreatedEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link GitHubWebHookEventHandler Event handler} for the {@link GitHubBranchCreatedEventDto created branch event}.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubBranchCreatedEventHandler implements GitHubWebHookEventHandler<GitHubBranchCreatedEventDto> {

    private static final Logger logger = LoggerFactory.getLogger(GitHubBranchCreatedEventHandler.class);

    private final GitHubWebHookCallback callback;

    public GitHubBranchCreatedEventHandler(GitHubWebHookCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean support(BaseGitHubWebHookEventDto event) {
        return event instanceof GitHubBranchCreatedEventDto;
    }

    @Override
    public Mono<Void> handle(GitHubRepositoryEntity repository, GitHubBranchCreatedEventDto event) {
        if (!event.isBranchRelated()) {
            return Mono.empty();
        }

        logger.info("The branch [{}] on the repository [{}] has been created.", event.getRef(), repository.getName());

        return callback.onCreatedBranch(repository, event.getRef());
    }
}
