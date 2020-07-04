package be.sgerard.i18n.model.workspace.dto;

import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A workspace represents the edition of translations related to a particular branch of a repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "Workspace",
        description = "A workspace is a place where users can define translations and then submit them for review. A workspace is based on a particular branch.")
@JsonDeserialize(builder = WorkspaceDto.Builder.class)
public class WorkspaceDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(WorkspaceEntity entity) {
        return builder()
                .id(entity.getId())
                .branch(entity.getBranch())
                .status(entity.getStatus());
    }

    @Schema(description = "Unique identifier of a workspace.", required = true)
    private final String id;

    @Schema(description = "The branch name on which the workspace is based on.", required = true)
    private final String branch;

    @Schema(description = "The current workspace status. First, the workspace is created, but not initialized. Then, " +
            "the workspace is initialized and all the translations are retrieved. Once they are edited, they are sent for review.", required = true)
    private final WorkspaceStatus status;

    private WorkspaceDto(Builder builder) {
        id = builder.id;
        branch = builder.branch;
        status = builder.status;
    }

    /**
     * Returns the unique id of this workspace.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the branch name of the repository containing those translations.
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Returns the current {@link WorkspaceStatus status}.
     */
    public WorkspaceStatus getStatus() {
        return status;
    }

    /**
     * Builder of {@link WorkspaceDto workspace DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String id;
        private String branch;
        private WorkspaceStatus status;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder branch(String branch) {
            this.branch = branch;
            return this;
        }

        public Builder status(WorkspaceStatus status) {
            this.status = status;
            return this;
        }

        public WorkspaceDto build() {
            return new WorkspaceDto(this);
        }
    }
}
