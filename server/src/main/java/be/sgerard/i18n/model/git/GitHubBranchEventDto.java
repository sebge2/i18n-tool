package be.sgerard.i18n.model.git;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sebastien Gerard
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public boolean isBranchRelated() {
        return REF_TYPE_BRANCH.equals(getRefType());
    }
}
