package be.sgerard.poc.githuboauth.controller.support.webhook;

/**
 * @author Sebastien Gerard
 */
public interface WebHookCallback {

    void onPullRequest(GitHubPullRequestEventDto pullRequest);

}
