package be.sgerard.i18n.model.workspace.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;

/**
 * {@link AbstractReviewEntity Review} of a workspace based on a GitHub repository. The review is thanks to a pull request.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
public class GitHubReviewEntity extends AbstractReviewEntity {

    /**
     * The branch used by the pull request.
     */
    @NotNull
    private String pullRequestBranch;

    /**
     * The GitHub pull request number of this review.
     */
    @NotNull
    private Integer pullRequestNumber;

    @PersistenceConstructor
    GitHubReviewEntity() {
    }

    public GitHubReviewEntity(String pullRequestBranch, int pullRequestNumber) {
        this.pullRequestBranch = pullRequestBranch;
        this.pullRequestNumber = pullRequestNumber;
    }

}
