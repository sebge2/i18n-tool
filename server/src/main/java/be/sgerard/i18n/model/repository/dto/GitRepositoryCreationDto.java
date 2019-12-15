package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;

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

    @JsonCreator
    public GitRepositoryCreationDto(@JsonProperty("location") String location,
                                    @JsonProperty("name") String name) {
        this.location = location;
        this.name = name;
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
     * Returns the location URI of this repository.
     */
    public URI getLocationAsURI() {
        return URI.create(getLocation());
    }

    /**
     * Returns the name of this repository.
     */
    public String getName() {
        return name;
    }
}
