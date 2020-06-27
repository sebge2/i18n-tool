package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Git repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "GitRepository", description = "Git Repository")
@JsonDeserialize(builder = GitRepositoryDto.Builder.class)
public class GitRepositoryDto extends RepositoryDto {

    public static Builder gitBuilder() {
        return new Builder();
    }

    @Schema(description = "Location URL of this repository", required = true)
    private final String location;

    @Schema(description = "The name of the default branch used to find translations", required = true)
    private final String defaultBranch;

    protected GitRepositoryDto(BaseBuilder<?, ?> builder) {
        super(builder);

        location = builder.location;
        defaultBranch = builder.defaultBranch;
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

    /**
     * Returns the name of the default branch used to find translations.
     */
    public String getDefaultBranch() {
        return defaultBranch;
    }

    /**
     * Builder of {@link GitRepositoryDto GIT repository DTO}.
     */
    public static abstract class BaseBuilder<R extends GitRepositoryDto, B extends BaseBuilder<R, B>> extends RepositoryDto.BaseBuilder<R, B> {
        private String location;
        private String defaultBranch;

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
