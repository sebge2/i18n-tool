package be.sgerard.i18n.model.repository.github.external;

import be.sgerard.i18n.service.BadRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Status of a {@link GitHubPullRequestDto GitHub pull-request}.
 *
 * @author Sebastien Gerard
 */
public enum GitHubPullRequestStatus {

    OPEN(false),

    CLOSED(true),

    MERGED(true);

    private final boolean finished;

    GitHubPullRequestStatus(boolean finished) {
        this.finished = finished;
    }

    /**
     * Returns whether the review is finished.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Maps the string to a status.
     */
    @JsonCreator
    public static GitHubPullRequestStatus fromString(String stringStatus) {
        for (GitHubPullRequestStatus status : GitHubPullRequestStatus.values()) {
            if (status.name().equalsIgnoreCase(stringStatus)) {
                return status;
            }
        }

        throw BadRequestException.cannotParseException(stringStatus, null);
    }
}
