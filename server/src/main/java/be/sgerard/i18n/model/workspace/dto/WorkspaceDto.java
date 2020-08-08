package be.sgerard.i18n.model.workspace.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

import static java.util.stream.Collectors.toList;

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

    public static Builder builder(WorkspaceEntity entity) {
        return builder()
                .id(entity.getId())
                .branch(entity.getBranch())
                .status(entity.getStatus())
                .repositoryId(entity.getRepository().getId())
                .repositoryName(entity.getRepository().getName())
                .repositoryType(entity.getRepository().getType())
                .files(entity.getFiles().stream().map(file -> BundleFileDto.builder(file).build()).collect(toList()));
    }

    @Schema(description = "Unique identifier of a workspace.", required = true)
    private final String id;

    @Schema(description = "The branch name on which the workspace is based on.", required = true)
    private final String branch;

    @Schema(description = "The current workspace status. First, the workspace is created, but not initialized. Then, " +
            "the workspace is initialized and all the translations are retrieved. Once they are edited, they are sent for review.", required = true)
    private final WorkspaceStatus status;

    @Schema(description = "The unique id of the associated repository.", required = true)
    private final String repositoryId;

    @Schema(description = "The name of the associated repository.", required = true)
    private final String repositoryName;

    @Schema(description = "The type of the associated repository.", required = true)
    private final RepositoryType repositoryType;

    @Schema(description = "All the bundle files contained in this workspace.", required = true)
    @Singular
    private final List<BundleFileDto> files;

    /**
     * Builder of {@link WorkspaceDto workspace DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
