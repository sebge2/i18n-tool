package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryType;
import org.springframework.data.annotation.PersistenceConstructor;


/**
 * Git {@link GitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
public class GitRepositoryEntity extends BaseGitRepositoryEntity {

    @PersistenceConstructor
    GitRepositoryEntity() {
        super();
    }

    public GitRepositoryEntity(String name, String location) {
        super(name);

        setLocation(location);
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GIT;
    }
}
