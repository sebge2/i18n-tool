package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.github.GitHubPullRequestEventDto;

/**
 * Callback when GitHub invoked the Web-hook.
 *
 * @author Sebastien Gerard
 */
public interface GitHubWebHookCallback {

    /**
     * Performs an action when the specified {@link GitHubPullRequestEventDto pull-request} has been updated.
     */
    default void onPullRequestUpdate(GitHubPullRequestEventDto event) throws Exception {
    }

    /**
     * Performs an action when the specified branch has been created.
     */
    default void onCreatedBranch(String branch) throws Exception {
    }

    /**
     * Performs an action when the specified branch has been deleted.
     */
    default void onDeletedBranch(String branch) throws Exception {
    }

}
