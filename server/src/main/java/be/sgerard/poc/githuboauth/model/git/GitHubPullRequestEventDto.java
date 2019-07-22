package be.sgerard.poc.githuboauth.model.git;

import be.sgerard.poc.githuboauth.model.git.PullRequestStatus;

import java.util.Map;
import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
public class GitHubPullRequestEventDto {

    private final String id;
    private final int number;
    private final PullRequestStatus status;
    private final Map<String, Object> allProperties;

    @SuppressWarnings("unchecked")
    public GitHubPullRequestEventDto(Map<String, Object> allProperties) {
        final Map<String, Object> pullRequestProperties = (Map<String, Object>) allProperties.get("pull_request");

        this.id = Objects.toString(pullRequestProperties.get("id"));
        this.number = Integer.valueOf(Objects.toString(pullRequestProperties.get("number")));
        this.status = PullRequestStatus.valueOf(Objects.toString(pullRequestProperties.get("state")));
        this.allProperties = allProperties;
    }

    public String getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public PullRequestStatus getStatus() {
        return status;
    }

    public Map<String, Object> getAllProperties() {
        return allProperties;
    }

}
