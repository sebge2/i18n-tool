package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Git {@link GitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
@Entity(name = "git_repository")
@DiscriminatorValue(value = "GIT")
public class GitRepositoryEntity extends BaseGitRepositoryEntity {

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

    @Override
    public GitRepositoryEntity deepCopy() {
        return fillEntity(new GitRepositoryEntity());
    }

    @Override
    protected GitRepositoryEntity fillEntity(RepositoryEntity copy) {
        return (GitRepositoryEntity) super.fillEntity(copy);
    }
}
