package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryType;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Git {@link GitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
@Document("repository")
@TypeAlias("GIT")
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
