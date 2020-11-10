package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.Optional;

/**
 * Request asking the creation of a Git repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "GitRepositoryCreationRequest", description = "Request asking the creation of a Git repository")
public class GitRepositoryCreationDto extends RepositoryCreationDto {

    @Schema(description = "Location URL of this repository", required = true)
    private final String location;

    @Schema(description = "Name of this repository", required = true)
    private final String name;

    @Schema(description = "Username to use to connect to the Git repository")
    private final String username;

    @Schema(description = "Password to connect to the Git repository")
    private final String password;

    @JsonCreator
    public GitRepositoryCreationDto(@JsonProperty("location") String location,
                                    @JsonProperty("name") String name,
                                    @JsonProperty("username") String username,
                                    @JsonProperty("password") String password) {
        this.location = location;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GIT;
    }

    /**
     * @see #location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @see #location
     */
    public URI getLocationAsURI() {
        return URI.create(getLocation());
    }

    /**
     * @see #name
     */
    public String getName() {
        return name;
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
