package be.sgerard.poc.githuboauth.model.git;

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
    public static PullRequestStatus fromString(String status) {
        for (PullRequestStatus type : PullRequestStatus.values()) {
            if (type.name().equalsIgnoreCase(status)) {
                return type;
            }
        }

        throw new IllegalArgumentException("There is no status [" + status + "].");
    }
}
