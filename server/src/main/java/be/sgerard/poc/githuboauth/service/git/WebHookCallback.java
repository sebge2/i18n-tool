package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.model.git.GitHubPullRequestEventDto;

/**
 * @author Sebastien Gerard
 */
public interface WebHookCallback {

    default void onPullRequestUpdate(GitHubPullRequestEventDto event) throws Exception {
    }

    default void onCreatedBranch(String branch) throws Exception {
    }

    ;

    default void onDeletedBranch(String branch) throws Exception {
    }

    ;
}
