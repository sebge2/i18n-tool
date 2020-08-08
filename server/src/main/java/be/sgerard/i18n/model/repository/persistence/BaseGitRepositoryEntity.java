package be.sgerard.i18n.model.repository.persistence;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

/**
 * Git {@link BaseGitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
public abstract class BaseGitRepositoryEntity extends RepositoryEntity {

    /**
     * Default branch name.
     */
    public static final String DEFAULT_BRANCH = "master";

    @NotNull
    private String location;

    @NotNull
    private String defaultBranch = DEFAULT_BRANCH;

    @NotNull
    public Pattern allowedBranches = Pattern.compile("^master|develop|release\\/[0-9]{4}.[0-9]{1,2}$");

    protected BaseGitRepositoryEntity() {
        super();
    }

    protected BaseGitRepositoryEntity(String name) {
        super(name);
    }

    /**
     * Returns the location URL of this repository.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location URL of this repository.
     */
    public BaseGitRepositoryEntity setLocation(String location) {
        this.location = location;
        return this;
    }

    /**
     * Returns the name of the default branch used to find translations.
     */
    public String getDefaultBranch() {
        return defaultBranch;
    }

    /**
     * Sets the name of the default branch used to find translations.
     */
    public BaseGitRepositoryEntity setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
        return this;
    }

    /**
     * Returns branches that can be scanned by this tool.
     */
    public Pattern getAllowedBranches() {
        return allowedBranches;
    }

    /**
     * Sets branches that can be scanned by this tool.
     */
    public BaseGitRepositoryEntity setAllowedBranches(Pattern allowedBranches) {
        this.allowedBranches = allowedBranches;
        return this;
    }
}
