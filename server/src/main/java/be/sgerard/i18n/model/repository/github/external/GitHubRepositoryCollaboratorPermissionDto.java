package be.sgerard.i18n.model.repository.github.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * Permission of a particular collaborator on a GitHub repository.
 *
 * @author Sebastien Gerard
 */
@Getter
public class GitHubRepositoryCollaboratorPermissionDto {

    /**
     * Unique id of the repository.
     */
    private final String permission;

    @JsonCreator
    public GitHubRepositoryCollaboratorPermissionDto(@JsonProperty("permission") String permission) {
        this.permission = permission;
    }

    /**
     * Returns whether the current user can write.
     */
    public boolean canWrite(){
        return asList("admin", "push", "write").contains(Objects.toString(permission, "").toLowerCase());
    }
}
