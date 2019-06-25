package be.sgerard.i18n.service.git;

import be.sgerard.i18n.model.git.GitHubPullRequestEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
@Component
public class PullRequestGithubWebHookEventHandler implements GithubWebHookEventHandler {

    public static final String PULL_REQUEST_EVENT = "pull_request";

    private final ObjectMapper objectMapper;

    public PullRequestGithubWebHookEventHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean support(String eventType) {
        return Objects.equals(PULL_REQUEST_EVENT, eventType);
    }

    @Override
    public void call(String eventType, String payload, WebHookCallback callback) throws Exception {
        callback.onPullRequestUpdate(deserialize(payload));
    }

    private GitHubPullRequestEventDto deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, GitHubPullRequestEventDto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse response.", e);
        }
    }
}
