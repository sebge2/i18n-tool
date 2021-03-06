package be.sgerard.i18n.model.workspace.dto;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

/**
 * A workspace represents the edition of translations related to a particular branch of a repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "Workspace",
        description = "A workspace is a place where users can define translations and then submit them for review. A workspace is based on a particular branch.")
@JsonDeserialize(builder = WorkspaceDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class WorkspaceDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Unique identifier of a workspace.", required = true)
    private final String id;

    @Schema(description = "The branch name on which the workspace is based on.", required = true)
    private final String branch;

    @Schema(description = "Flag indicating whether this workspace is the default one of the repository.", required = true)
    private final boolean defaultWorkspace;

    @Schema(description = "The current workspace status. First, the workspace is created, but not initialized. Then, " +
            "the workspace is initialized and all the translations are retrieved. Once they are edited, they are sent for review.", required = true)
    private final WorkspaceStatus status;

    @Schema(description = "The unique id of the associated repository.", required = true)
    private final String repositoryId;

    @Schema(description = "The unique name of the associated repository.", required = true)
    private final String repositoryName;

    @Schema(description = "The current status of the associated repository.", required = true)
    private final RepositoryStatus repositoryStatus;

    @Schema(description = "The type of the associated repository.", required = true)
    private final RepositoryType repositoryType;

    @Schema(description = "Number of bundles keys in this workspace.", required = true)
    private final long numberBundleKeys;

    @Schema(description = "Indicates whether there are some modifications associated to this workspace.", required = true)
    private final boolean dirty;

    @Schema(description = "The time when the workspace was synchronized with the repository for the last time.")
    private final Instant lastSynchronization;

    @Schema(description = "Information about the current review in progress.")
    private final ReviewDto review;

    /**
     * @see #review
     */
    public Optional<ReviewDto> getReview() {
        return Optional.ofNullable(review);
    }

    /**
     * Builder of {@link WorkspaceDto workspace DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
