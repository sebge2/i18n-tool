package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * GitHub repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "GitHubRepository", description = "GitHub Repository")
@JsonDeserialize(builder = GitHubRepositoryDto.Builder.class)
@Getter
public class GitHubRepositoryDto extends GitRepositoryDto {

    public static Builder gitHubBuilder() {
        return new Builder();
    }

    @Schema(description = "GitHub username owner of the repository.", required = true)
    private final String username;

    @Schema(description = "GitHub repository name.", required = true)
    private final String repository;

    private GitHubRepositoryDto(Builder builder) {
        super(builder);

        username = builder.username;
        repository = builder.repository;
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GITHUB;
    }

    /**
     * Builder of {@link GitHubRepositoryDto GitHub repository DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder extends GitRepositoryDto.BaseBuilder<GitHubRepositoryDto, Builder> {

        private String username;
        private String repository;

        public Builder() {
        }

        public Builder username(String username) {
            this.username = username;
            return self();
        }

        public Builder repository(String repository) {
            this.repository = repository;
            return self();
        }

        @Override
        public GitHubRepositoryDto build() {
            return new GitHubRepositoryDto(this);
        }
    }
}
