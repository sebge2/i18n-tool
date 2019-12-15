package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;


/**
 * Git {@link GitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
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
