package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

/**
 * GitHub repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "GitHubRepository", description = "GitHub Repository")
@JsonDeserialize(builder = GitHubRepositoryDto.Builder.class)
public class GitHubRepositoryDto extends GitRepositoryDto {

    public static Builder gitHubBuilder() {
        return new Builder();
    }

    @Schema(description = "Access key to use to access this repository", required = true)
    private final String accessKey;

    @Schema(description = "Access key to use to access this repository")
    private final String webHookSecret;

    private GitHubRepositoryDto(Builder builder) {
        super(builder);

        accessKey = builder.accessKey;
        webHookSecret = builder.webHookSecret;
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GITHUB;
    }

    /**
     * Returns the access key to use to access this repository.
     */
    public Optional<String> getAccessKey() {
        return Optional.ofNullable(accessKey);
    }

    /**
     * Returns the secret shared when GitHub access this app via a web-hook.
     */
    public Optional<String> getWebHookSecret() {
        return Optional.ofNullable(webHookSecret);
    }

    /**
     * Builder of {@link GitHubRepositoryDto GitHub repository DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder extends GitRepositoryDto.BaseBuilder<GitHubRepositoryDto, Builder> {

        private String accessKey;
        private String webHookSecret;

        public Builder() {
        }

        public Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return self();
        }

        public Builder webHookSecret(String webHookSecret) {
            this.webHookSecret = webHookSecret;
            return self();
        }

        @Override
        public GitHubRepositoryDto build() {
            return new GitHubRepositoryDto(this);
        }
    }
}
