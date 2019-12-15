package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Git repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "GitRepository", description = "Git Repository")
@JsonDeserialize(builder = GitRepositoryDto.Builder.class)
@Getter
public class GitRepositoryDto extends RepositoryDto {

    public static Builder gitBuilder() {
        return new Builder();
    }

    @Schema(description = "Location URL of this repository", required = true)
    private final String location;

    @Schema(description = "The name of the default branch used to find translations", required = true)
    private final String defaultBranch;

    @Schema(description = "Regex specifying branches that can be scanned by this tool.", required = true)
    private final String allowedBranches;

    protected GitRepositoryDto(BaseBuilder<?, ?> builder) {
        super(builder);

        location = builder.location;
        defaultBranch = builder.defaultBranch;
        allowedBranches = builder.allowedBranches;
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GIT;
    }

    /**
     * Builder of {@link GitRepositoryDto GIT repository DTO}.
     */
    public static abstract class BaseBuilder<R extends GitRepositoryDto, B extends BaseBuilder<R, B>> extends RepositoryDto.BaseBuilder<R, B> {
        private String location;
        private String defaultBranch;
        private String allowedBranches;

        protected BaseBuilder() {
        }

        public B location(String location) {
            this.location = location;
            return self();
        }

        public B defaultBranch(String defaultBranch) {
            this.defaultBranch = defaultBranch;
            return self();
        }

        public B allowedBranches(String allowedBranches) {
            this.allowedBranches = allowedBranches;
            return self();
        }
    }

    /**
     * Builder of {@link GitRepositoryDto GIT repository DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder extends BaseBuilder<GitRepositoryDto, Builder> {

        public Builder() {
        }

        @Override
        public GitRepositoryDto build() {
            return new GitRepositoryDto(this);
        }
    }
}
