package be.sgerard.i18n.model.git;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author Sebastien Gerard
 */
public enum PullRequestStatus {

    OPEN(false),

    CLOSED(true),

    MERGED(true);

    private final boolean finished;

    PullRequestStatus(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }

    @JsonCreator
    public static PullRequestStatus fromString(String stringStatus) {
        for (PullRequestStatus status : PullRequestStatus.values()) {
            if (status.name().equalsIgnoreCase(stringStatus)) {
                return status;
            }
        }

        throw new IllegalArgumentException("There is no status [" + stringStatus + "].");
    }
}
