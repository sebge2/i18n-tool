package be.sgerard.poc.githuboauth.model.git;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Sebastien Gerard
 */
public class GitHubBranchEventDto {

    public static final String REF_TYPE_BRANCH = "branch";

    private final String refType;
    private final String ref;

    @JsonCreator
    public GitHubBranchEventDto(@JsonProperty("ref_type") String refType,
                                @JsonProperty("ref") String ref) {
        this.refType = refType;
        this.ref = ref;
    }

    public String getRefType() {
        return refType;
    }

    public String getRef() {
        return ref;
    }

    public boolean isBranchRelated(){
        return REF_TYPE_BRANCH.equals(getRefType());
    }
}
