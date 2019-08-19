package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.model.git.GitHubBranchEventDto;
import be.sgerard.poc.githuboauth.model.git.GitHubPullRequestEventDto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeWebHookCallback implements WebHookCallback {

    private final List<WebHookCallback> callbacks;

    public CompositeWebHookCallback(List<WebHookCallback> callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void onPullRequest(GitHubPullRequestEventDto event) throws Exception {
        for (WebHookCallback callback : callbacks) {
            callback.onPullRequest(event);
        }
    }

    @Override
    public void onCreatedBranch(GitHubBranchEventDto event) throws Exception {
        for (WebHookCallback callback : callbacks) {
            callback.onCreatedBranch(event);
        }
    }

    @Override
    public void onDeletedBranch(GitHubBranchEventDto event) throws Exception {
        for (WebHookCallback callback : callbacks) {
            callback.onDeletedBranch(event);
        }
    }
}
