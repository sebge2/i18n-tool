package be.sgerard.i18n.service.git;

import be.sgerard.i18n.model.github.GitHubPullRequestEventDto;

/**
 * @author Sebastien Gerard
 */
public interface WebHookCallback {

    default void onPullRequestUpdate(GitHubPullRequestEventDto event) throws Exception {
    }

    default void onCreatedBranch(String branch) throws Exception {
    }

    default void onDeletedBranch(String branch) throws Exception {
    }

}
