package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Request asking the creation of a repository.
 *
 * @author Sebastien Gerard
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitRepositoryCreationDto.class, name = "GIT"),
        @JsonSubTypes.Type(value = GitHubRepositoryCreationDto.class, name = "GITHUB")
})
@ApiModel(value = "RepositoryCreationRequest", description = "Request asking the creation of a repository")
public abstract class RepositoryCreationDto {

    protected RepositoryCreationDto() {
    }

    /**
     * Returns the {@link RepositoryType type} of this repository.
     */
    @ApiModelProperty(notes = "Type of this repository", required = true)
    public abstract RepositoryType getType();
}
