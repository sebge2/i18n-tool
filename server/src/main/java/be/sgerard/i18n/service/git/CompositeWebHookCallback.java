package be.sgerard.i18n.service.git;

import be.sgerard.i18n.model.github.GitHubPullRequestEventDto;
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
    public void onPullRequestUpdate(GitHubPullRequestEventDto event) throws Exception {
        for (WebHookCallback callback : callbacks) {
            callback.onPullRequestUpdate(event);
        }
    }

    @Override
    public void onCreatedBranch(String branch) throws Exception {
        for (WebHookCallback callback : callbacks) {
            callback.onCreatedBranch(branch);
        }
    }

    @Override
    public void onDeletedBranch(String branch) throws Exception {
        for (WebHookCallback callback : callbacks) {
            callback.onDeletedBranch(branch);
        }
    }
}
