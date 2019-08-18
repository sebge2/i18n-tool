package be.sgerard.poc.githuboauth.model.git;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        private static final Logger logger = LoggerFactory.getLogger(PullRequestStatus.class);

    @JsonCreator
    public static PullRequestStatus fromString(String status) {
        for (PullRequestStatus type : PullRequestStatus.values()) {
            if (type.name().equalsIgnoreCase(status)) {
                logger.error("status " + status + " match " + type, new Exception().fillInStackTrace() );
                return type;
            }
        }

        throw new IllegalArgumentException("There is no status [" + status + "].");
    }
}
