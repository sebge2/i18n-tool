package be.sgerard.i18n.service.git;

import be.sgerard.i18n.service.github.GitHubWebHookCallback;

/**
 * @author Sebastien Gerard
 */
public interface GithubWebHookEventHandler {

    boolean support(String eventType);

    void call(String eventType, String payload, GitHubWebHookCallback callback) throws Exception;
}
