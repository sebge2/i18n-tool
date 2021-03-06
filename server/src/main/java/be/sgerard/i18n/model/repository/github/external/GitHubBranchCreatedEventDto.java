package be.sgerard.i18n.model.repository.github.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link BaseGitHubWebHookEventDto Event DTO} concerning a branch creation.
 *
 * @author Sebastien Gerard
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubBranchCreatedEventDto extends BaseGitHubWebHookEventDto {

    public static final String REF_TYPE_BRANCH = "branch";

    private final String refType;
    private final String ref;

    @JsonCreator
    public GitHubBranchCreatedEventDto(@JsonProperty("repository") Repository repository,
                                       @JsonProperty("ref_type") String refType,
                                       @JsonProperty("ref") String ref) {
        super(repository);
        this.refType = refType;
        this.ref = ref;
    }

    /**
     * Returns the type of reference (ex: tag, branch).
     */
    public String getRefType() {
        return refType;
    }

    /**
     * Returns the reference.
     */
    public String getRef() {
        return ref;
    }

    /**
     * Returns whether the reference is a branch.
     */
    public boolean isBranchRelated() {
        return REF_TYPE_BRANCH.equals(getRefType());
    }
}
