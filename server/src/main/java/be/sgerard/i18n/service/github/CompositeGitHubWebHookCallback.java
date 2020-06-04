package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.github.GitHubPullRequestEventDto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Composite {@link GitHubWebHookCallback GitHub Webhook callback}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeGitHubWebHookCallback implements GitHubWebHookCallback {

    private final List<GitHubWebHookCallback> callbacks;

    public CompositeGitHubWebHookCallback(List<GitHubWebHookCallback> callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void onPullRequestUpdate(GitHubPullRequestEventDto event) throws Exception {
        for (GitHubWebHookCallback callback : callbacks) {
            callback.onPullRequestUpdate(event);
        }
    }

    @Override
    public void onCreatedBranch(String branch) throws Exception {
        for (GitHubWebHookCallback callback : callbacks) {
            callback.onCreatedBranch(branch);
        }
    }

    @Override
    public void onDeletedBranch(String branch) throws Exception {
        for (GitHubWebHookCallback callback : callbacks) {
            callback.onDeletedBranch(branch);
        }
    }
}
