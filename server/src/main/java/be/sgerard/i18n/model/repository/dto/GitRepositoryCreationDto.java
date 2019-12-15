package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Request asking the creation of a Git repository.
 *
 * @author Sebastien Gerard
 */
@ApiModel(value = "GitRepositoryCreationRequest", description = "Request asking the creation of a Git repository")
public class GitRepositoryCreationDto extends RepositoryCreationDto {

    @ApiModelProperty(notes = "Location URL of this repository", required = true)
    private final String location;

    @JsonCreator
    public GitRepositoryCreationDto(@JsonProperty("location") String location) {
        this.location = location;
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
}
