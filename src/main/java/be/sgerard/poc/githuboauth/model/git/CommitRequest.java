package be.sgerard.poc.githuboauth.model.git;

/**
 * @author Sebastien Gerard
 */
public class CommitRequest {

    private final String message;
    private final String authorName;
    private final String authorEmail;

    public CommitRequest(String message, String authorName, String authorEmail) {
        this.message = message;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }
}
