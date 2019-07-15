package be.sgerard.poc.githuboauth.controller.webhook;

/**
 * @author Sebastien Gerard
 */
public interface WebHookCallback {

    void onPullRequest(GitHubPullRequestEventDto pullRequest);

}
