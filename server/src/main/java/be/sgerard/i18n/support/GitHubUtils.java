package be.sgerard.i18n.support;

import java.net.URI;

/**
 * Bunch of utility methods for GitHub.
 *
 * @author Sebastien Gerard
 */
public final class GitHubUtils {

    private GitHubUtils() {
    }

    /**
     * Creates the GitHub repository URI based on the owner and the repository name.
     */
    public static URI createGitHubUrl(String owner, String repository){
        return URI.create(String.format("https://github.com/%s/%s.git", owner, repository));
    }
}
