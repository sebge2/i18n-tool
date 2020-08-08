package be.sgerard.i18n.model.repository.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

/**
 * Git {@link BaseGitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class BaseGitRepositoryEntity extends RepositoryEntity {

    /**
     * Default branch name.
     */
    public static final String DEFAULT_BRANCH = "master";

    /**
     * The location URL of this repository.
     */
    @NotNull
    private String location;

    /**
     * The name of the default branch used to find translations.
     */
    @NotNull
    private String defaultBranch = DEFAULT_BRANCH;

    /**
     * Branches that can be scanned by this tool.
     */
    @NotNull
    public Pattern allowedBranches = Pattern.compile("^master|develop|release\\/[0-9]{4}.[0-9]{1,2}$");

    protected BaseGitRepositoryEntity() {
        super();
    }

    protected BaseGitRepositoryEntity(String name) {
        super(name);
    }
}
