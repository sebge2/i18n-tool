package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.model.git.GitHubBranchEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * @author Sebastien Gerard
 */
@Component
public class BranchGithubWebHookEventHandler implements GithubWebHookEventHandler {

    public static final String CREATED_EVENT = "create";

    public static final String DELETED_EVENT = "delete";

    private static final Logger logger = LoggerFactory.getLogger(BranchGithubWebHookEventHandler.class);

    private final ObjectMapper objectMapper;

    public BranchGithubWebHookEventHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean support(String eventType) {
        return asList(CREATED_EVENT, DELETED_EVENT).contains(eventType);
    }

    @Override
    public void call(String eventType, String payload, WebHookCallback callback) throws Exception {
        final GitHubBranchEventDto event = objectMapper.readValue(payload, GitHubBranchEventDto.class);

        if (event.isBranchRelated() && Objects.equals(eventType, CREATED_EVENT)) {
            callback.onCreatedBranch(event.getRef());
        } else if (event.isBranchRelated() && Objects.equals(eventType, DELETED_EVENT)) {
            callback.onDeletedBranch(event.getRef());
        } else {
            logger.info("Ignore event type [" + eventType + "] for ref type [" + event.getRefType() + "].");
        }
    }
}
