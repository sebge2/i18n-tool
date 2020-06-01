package be.sgerard.i18n.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Pull-request of a GitHub repository.
 *
 * @author Sebastien Gerard
 */
@ApiModel(value = "GitHubPullRequest", description = "Pull-request on GitHub.")
@JsonDeserialize(builder = GitHubPullRequestDto.Builder.class)
public class GitHubPullRequestDto {

    public static Builder builder() {
        return new Builder();
    }

    @ApiModelProperty(notes = "Name of the repository associated to this request", required = true)
    private final String repositoryName;

    @ApiModelProperty(notes = "Unique id number of this request", required = true)
    private final int number;

    @ApiModelProperty(notes = "The name of the branch containing changes", required = true)
    private final String currentBranch;

    @ApiModelProperty(notes = "The name of the branch where changes should be applied", required = true)
    private final String targetBranch;

    @ApiModelProperty(notes = "Current status of this request", required = true)
    private final GitHubPullRequestStatus status;

    private GitHubPullRequestDto(Builder builder) {
        repositoryName = builder.repositoryName;
        number = builder.number;
        currentBranch = builder.currentBranch;
        targetBranch = builder.targetBranch;
        status = builder.status;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public int getNumber() {
        return number;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public GitHubPullRequestStatus getStatus() {
        return status;
    }

    /**
     * Builder of {@link GitHubPullRequestDto pull-request DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String repositoryName;
        private int number;
        private String currentBranch;
        private String targetBranch;
        private GitHubPullRequestStatus status;

        private Builder() {
        }

        public Builder repositoryName(String repositoryName) {
            this.repositoryName = repositoryName;
            return this;
        }

        public Builder number(int number) {
            this.number = number;
            return this;
        }

        public Builder currentBranch(String currentBranch) {
            this.currentBranch = currentBranch;
            return this;
        }

        public Builder targetBranch(String targetBranch) {
            this.targetBranch = targetBranch;
            return this;
        }

        public Builder status(GitHubPullRequestStatus status) {
            this.status = status;
            return this;
        }

        public GitHubPullRequestDto build() {
            return new GitHubPullRequestDto(this);
        }
    }
}
