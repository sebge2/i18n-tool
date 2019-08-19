package be.sgerard.poc.githuboauth.service.git;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeGithubWebHookEventHandler implements GithubWebHookEventHandler {

    private final List<GithubWebHookEventHandler> handlers;

    public CompositeGithubWebHookEventHandler(List<GithubWebHookEventHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean support(String eventType) {
        return handlers.stream().anyMatch(handler -> handler.support(eventType));
    }

    @Override
    public void call(String eventType, String payload, WebHookCallback callback) throws Exception {
        for (GithubWebHookEventHandler handler : handlers) {
            if (handler.support(eventType)) {
                handler.call(eventType, payload, callback);
            }
        }
    }
}
