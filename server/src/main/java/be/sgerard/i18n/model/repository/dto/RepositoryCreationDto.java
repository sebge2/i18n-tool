package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(name = "RepositoryCreationRequest", description = "Request asking the creation of a repository")
public abstract class RepositoryCreationDto {

    protected RepositoryCreationDto() {
    }

    /**
     * Returns the {@link RepositoryType type} of this repository.
     */
    @Schema(description = "Type of this repository", required = true)
    public abstract RepositoryType getType();
}
