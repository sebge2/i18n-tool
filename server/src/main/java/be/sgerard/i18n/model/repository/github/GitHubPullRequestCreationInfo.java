package be.sgerard.i18n.model.repository.github;

import lombok.Value;

/**
 * Information about the Pull-request to create.
 *
 * @author Sebastien Gerard
 */
@Value
@SuppressWarnings("RedundantModifiersValueLombok")
public class GitHubPullRequestCreationInfo {

    /**
     * Title of the Pull-Request.
     */
    private final String title;

    /**
     * The branch to merge.
     */
    private final String currentBranch;

    /**
     * The branch targets of the merge.
     */
    private final String targetBranch;
}
