package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

/**
 * Request asking the update of a Git repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "GitRepositoryPatchRequest", description = "Request asking the update of a Git repository")
@JsonDeserialize(builder = GitRepositoryPatchDto.Builder.class)
public class GitRepositoryPatchDto extends RepositoryPatchDto {

    public static Builder gitBuilder() {
        return new Builder();
    }

    @Schema(description = "The name of the default branch used to find translations", required = true)
    private final String defaultBranch;


    @Schema(description = "Regex specifying branches that can be scanned by this tool.", required = true)
    private final String allowedBranches;

    protected GitRepositoryPatchDto(BaseBuilder<?, ?> builder) {
        super(builder);

        this.defaultBranch = builder.defaultBranch;
        this.allowedBranches = builder.allowedBranches;
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GIT;
    }

    /**
     * @see #defaultBranch
     */
    public Optional<String> getDefaultBranch() {
        return Optional.ofNullable(defaultBranch);
    }

    /**
     * @see #allowedBranches
     */
    public Optional<String> getAllowedBranches() {
        return Optional.ofNullable(allowedBranches);
    }

    /**
     * Builder of {@link GitRepositoryPatchDto GIT repository patch DTO}.
     */
    public static abstract class BaseBuilder<R extends GitRepositoryPatchDto, B extends GitRepositoryPatchDto.BaseBuilder<R, B>> extends RepositoryPatchDto.BaseBuilder<R, B> {

        private String defaultBranch;
        private String allowedBranches;

        protected BaseBuilder() {
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
     * Builder of {@link GitRepositoryPatchDto GIT repository patch DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder extends BaseBuilder<GitRepositoryPatchDto, GitRepositoryPatchDto.Builder> {

        public Builder() {
        }

        @Override
        public GitRepositoryPatchDto build() {
            return new GitRepositoryPatchDto(this);
        }
    }
}
