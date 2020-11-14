package be.sgerard.i18n.model.repository.github.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link BaseGitHubWebHookEventDto Event DTO} concerning a pull-request.
 *
 * @author Sebastien Gerard
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPullRequestEventDto extends BaseGitHubWebHookEventDto {

    private final PullRequest pullRequest;

    @JsonCreator
    public GitHubPullRequestEventDto(@JsonProperty("repository") Repository repository,
                                     @JsonProperty("pull_request") PullRequest pullRequest) {
        super(repository);

        this.pullRequest = pullRequest;
    }

    /**
     * Returns information about the {@link PullRequest pull request}.
     */
    public PullRequest getPullRequest() {
        return pullRequest;
    }

    /**
     * Information about the {@link PullRequest pull request}.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class PullRequest {

        private final String id;
        private final int number;
        private final GitHubPullRequestStatus status;

        @JsonCreator
        public PullRequest(@JsonProperty("id") String id,
                           @JsonProperty("number") int number,
                           @JsonProperty("state") GitHubPullRequestStatus status) {
            this.id = id;
            this.number = number;
            this.status = status;
        }

        /**
         * Returns the unique id of the pull-request.
         */
        public String getId() {
            return id;
        }

        /**
         * Returns the number of the pull-request.
         */
        public int getNumber() {
            return number;
        }

        /**
         * Returns the {@link GitHubPullRequestStatus status} of this pull-request.
         */
        public GitHubPullRequestStatus getStatus() {
            return status;
        }
    }
}
