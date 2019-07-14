package be.sgerard.poc.githuboauth.model.i18n.dto;

import be.sgerard.poc.githuboauth.model.i18n.WorkspaceStatus;
import be.sgerard.poc.githuboauth.model.i18n.persistence.WorkspaceEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.Instant;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "A workspace is a place where users can define translations and then submit them for review. A workspace is based on a particular branch.")
public class WorkspaceDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(WorkspaceEntity entity) {
        return builder()
                .id(entity.getId())
                .branch(entity.getBranch())
                .status(entity.getStatus())
                .pullRequestBranch(entity.getPullRequestBranch().orElse(null))
                .pullRequestNumber(entity.getPullRequestNumber().orElse(null))
                .initializationTime(entity.getInitializationTime().orElse(null));
    }

    @ApiModelProperty(notes = "Unique identifier of a workspace.", required = true)
    private final String id;

    @ApiModelProperty(notes = "The branch name on which the workspace is based on.", required = true)
    private final String branch;

    @ApiModelProperty(notes = "The current workspace status. First, the workspace is created, but not initialized. Then, " +
            "the workspace is initialized and all the translations are retrieved. Once they are edited, they are sent for review.", required = true)
    private final WorkspaceStatus status;

    @ApiModelProperty(notes = "The temporary branch where changes will be committed.", required = true)
    private final String pullRequestBranch;

    @ApiModelProperty(notes = "The current pull request number associated to this workspace. " +
            "This pull request is associated to 'pullRequestBranch'.")
    private final Integer pullRequestNumber;

    @ApiModelProperty(notes = "The time when this workspace was initialized. All translations are retrieved from the branch at that time.")
    private final Instant initializationTime;

    private WorkspaceDto(Builder builder) {
        id = builder.id;
        branch = builder.branch;
        status = builder.status;
        pullRequestBranch = builder.pullRequestBranch;
        pullRequestNumber = builder.pullRequestNumber;
        initializationTime = builder.initializationTime;
    }

    public String getId() {
        return id;
    }

    public String getBranch() {
        return branch;
    }

    public WorkspaceStatus getStatus() {
        return status;
    }

    public String getPullRequestBranch() {
        return pullRequestBranch;
    }

    public Integer getPullRequestNumber() {
        return pullRequestNumber;
    }

    public Instant getInitializationTime() {
        return initializationTime;
    }

    public static final class Builder {
        private String id;
        private String branch;
        private WorkspaceStatus status;
        private String pullRequestBranch;
        private Integer pullRequestNumber;
        private Instant initializationTime;

        private Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder branch(String val) {
            branch = val;
            return this;
        }

        public Builder status(WorkspaceStatus val) {
            status = val;
            return this;
        }

        public Builder pullRequestBranch(String val) {
            pullRequestBranch = val;
            return this;
        }

        public Builder pullRequestNumber(Integer val) {
            pullRequestNumber = val;
            return this;
        }

        public Builder initializationTime(Instant val) {
            initializationTime = val;
            return this;
        }

        public WorkspaceDto build() {
            return new WorkspaceDto(this);
        }
    }
}
