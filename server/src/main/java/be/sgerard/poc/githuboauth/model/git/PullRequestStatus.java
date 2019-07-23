package be.sgerard.poc.githuboauth.model.git;

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
}
