package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

/**
 * Git {@link GitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
@Entity(name = "git_repository")
@DiscriminatorValue(value = "GIT")
public class GitRepositoryEntity extends RepositoryEntity {

    /**
     * Default branch name.
     */
    public static final String DEFAULT_BRANCH = "master";

    @NotNull
    @Column(nullable = false)
    private String location;

    @NotNull
    @Column(nullable = false)
    private String defaultBranch = DEFAULT_BRANCH;

    GitRepositoryEntity() {
        super();
    }

    public GitRepositoryEntity(String name) {
        super(name);
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GIT;
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
    public GitRepositoryEntity setLocation(String location) {
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
    public GitRepositoryEntity setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
        return this;
    }

    public Pattern getAllowedBranchesPattern() {
        return BRANCHES_TO_KEEP;
    }

    @Override
    public GitRepositoryEntity deepCopy() {
        return new GitRepositoryEntity();
    }

    @Override
    protected void fillEntity(RepositoryEntity copy) {
        ((GitHubRepositoryEntity) copy)
                .setDefaultBranch(this.defaultBranch)
                .setLocation(this.location);

        super.fillEntity(copy);
    }
}
