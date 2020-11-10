package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.support.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

/**
 * Request asking the update of a GitHub repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "GitHubRepositoryPatchRequest", description = "Request asking the update of a GitHub repository")
@JsonDeserialize(builder = GitHubRepositoryPatchDto.Builder.class)
public class GitHubRepositoryPatchDto extends BaseGitRepositoryPatchDto {

    public static GitHubRepositoryPatchDto.Builder gitHubBuilder() {
        return new GitHubRepositoryPatchDto.Builder();
    }

    @Schema(description = "Access key to use to access this repository (empty means no access key)")
    private final String accessKey;

    @Schema(description = "Access key to use to access this repository (empty means no password)")
    private final String webHookSecret;

    private GitHubRepositoryPatchDto(Builder builder) {
        super(builder);

        this.accessKey = builder.accessKey;
        this.webHookSecret = builder.webHookSecret;
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
     * Returns the access key to use after patching the specified repository.
     */
    public Optional<String> getUpdateAccessKey(GitHubRepositoryEntity repository) {
        return getAccessKey().or(repository::getAccessKey).filter(StringUtils::isNotEmptyString);
    }

    /**
     * Returns the secret shared when GitHub access this app via a web-hook.
     */
    public Optional<String> getWebHookSecret() {
        return Optional.ofNullable(webHookSecret);
    }

    /**
     * Returns the web-hook secret to use after patching the specified repository.
     */
    public Optional<String> getUpdateWebHookSecret(GitHubRepositoryEntity repository) {
        return getWebHookSecret().or(repository::getWebHookSecret).filter(StringUtils::isNotEmptyString);
    }

    /**
     * Builder of {@link GitHubRepositoryDto GitHub repository DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder extends GitRepositoryPatchDto.BaseBuilder<GitHubRepositoryPatchDto, GitHubRepositoryPatchDto.Builder> {

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
        public GitHubRepositoryPatchDto build() {
            return new GitHubRepositoryPatchDto(this);
        }
    }
}
