package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.service.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.service.github.external.GitHubBranchDeletedEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link GitHubWebHookEventHandler Event handler} for the {@link GitHubBranchDeletedEventDto deleted branch event}.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubBranchDeletedEventHandler implements GitHubWebHookEventHandler<GitHubBranchDeletedEventDto> {

    private static final Logger logger = LoggerFactory.getLogger(GitHubBranchDeletedEventHandler.class);

    private final GitHubWebHookCallback callback;

    public GitHubBranchDeletedEventHandler(GitHubWebHookCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean support(BaseGitHubWebHookEventDto event) {
        return event instanceof GitHubBranchDeletedEventDto;
    }

    @Override
    public Mono<Void> handle(GitHubRepositoryEntity repository, GitHubBranchDeletedEventDto event) {
        if (!event.isBranchRelated()) {
            return Mono.empty();
        }

        logger.info("The branch [{}] on the repository [{}] has been deleted.", event.getRef(), repository.getName());

        return callback.onDeletedBranch(repository, event.getRef());
    }
}
