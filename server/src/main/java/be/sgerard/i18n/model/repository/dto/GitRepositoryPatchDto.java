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
public class GitRepositoryPatchDto extends BaseGitRepositoryPatchDto {

    public static Builder gitBuilder() {
        return new Builder();
    }

    @Schema(description = "The unique name of this repository.", required = true)
    private final String name;


    protected GitRepositoryPatchDto(Builder builder) {
        super(builder);

        this.name = builder.name;
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GIT;
    }

    /**
     * @see #name
     */
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    /**
     * Builder of {@link GitRepositoryPatchDto GIT repository patch DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder extends BaseBuilder<GitRepositoryPatchDto, GitRepositoryPatchDto.Builder> {

        private String name;

        public Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public GitRepositoryPatchDto build() {
            return new GitRepositoryPatchDto(this);
        }
    }
}
