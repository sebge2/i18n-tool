package be.sgerard.i18n.model.workspace;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

import javax.validation.constraints.NotNull;

/**
 * {@link AbstractReviewEntity Review} of a workspace based on a GitHub repository. The review is thanks to a pull request.
 *
 * @author Sebastien Gerard
 */
@TypeAlias("GITHUB")
public class GitHubReviewEntity extends AbstractReviewEntity {

    @NotNull
    private String pullRequestBranch;

    @NotNull
    private Integer pullRequestNumber;

    @PersistenceConstructor
    GitHubReviewEntity() {
    }

    public GitHubReviewEntity(String pullRequestBranch, int pullRequestNumber) {
        this.pullRequestBranch = pullRequestBranch;
        this.pullRequestNumber = pullRequestNumber;
    }

    /**
     * Returns the branch used by the pull request.
     */
    public String getPullRequestBranch() {
        return pullRequestBranch;
    }

    /**
     * Sets the branch used by the pull request.
     */
    public void setPullRequestBranch(String pullRequestBranch) {
        this.pullRequestBranch = pullRequestBranch;
    }

    /**
     * Returns the GitHub pull request number of this review.
     */
    public Integer getPullRequestNumber() {
        return pullRequestNumber;
    }

    /**
     * Sets the GitHub pull request number of this review.
     */
    public void setPullRequestNumber(Integer pullRequestNumber) {
        this.pullRequestNumber = pullRequestNumber;
    }

}
