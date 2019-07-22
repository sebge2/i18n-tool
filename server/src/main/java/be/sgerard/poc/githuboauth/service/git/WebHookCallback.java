package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.model.git.GitHubPullRequestEventDto;

/**
 * @author Sebastien Gerard
 */
public interface WebHookCallback {

    void onPullRequest(GitHubPullRequestEventDto pullRequest);

}
