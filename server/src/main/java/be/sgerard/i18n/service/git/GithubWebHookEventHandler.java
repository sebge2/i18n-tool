package be.sgerard.i18n.service.git;

/**
 * @author Sebastien Gerard
 */
public interface GithubWebHookEventHandler {

    boolean support(String eventType);

    void call(String eventType, String payload, WebHookCallback callback) throws Exception;
}
