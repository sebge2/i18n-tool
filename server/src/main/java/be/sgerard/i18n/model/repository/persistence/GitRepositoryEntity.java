package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
import java.util.Optional;


/**
 * Git {@link GitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
public class GitRepositoryEntity extends BaseGitRepositoryEntity {

    /**
     * Username to use to connect to the Git repository.
     */
    @NotNull
    private String username;

    /**
     * Password to connect to the Git repository.
     */
    @NotNull
    private String password;

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

    /**
     * @see #username
     */
    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    /**
     * @see #password
     */
    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }
}
