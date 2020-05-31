package be.sgerard.i18n.model.workspace;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * {@link AbstractReviewEntity Review} of a workspace based on a GitHub repository. The review is thanks to a pull request.
 *
 * @author Sebastien Gerard
 */
@Entity
@DiscriminatorValue(value = "GITHUB")
public class GitHubReviewEntity extends AbstractReviewEntity {

    @Column(nullable = false)
    private String pullRequestBranch;

    @Column(nullable = false)
    private Integer pullRequestNumber;

    GitHubReviewEntity() {
    }

    public GitHubReviewEntity(WorkspaceEntity workspace, String pullRequestBranch, Integer pullRequestNumber) {
        super(workspace);

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
