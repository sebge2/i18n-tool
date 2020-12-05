package be.sgerard.i18n.model.repository.snapshot;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import java.util.Optional;

/**
 * Snapshot of a {@link GitHubRepositoryEntity Git repository}.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = GitHubRepositorySnapshotDto.Builder.class)
@Getter
public class GitHubRepositorySnapshotDto extends BaseGitRepositorySnapshotDto {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * @see GitHubRepositoryEntity#getUsername()
     */
    private final String username;

    /**
     * @see GitHubRepositoryEntity#getRepository()
     */
    private final String repository;

    /**
     * @see GitHubRepositoryEntity#getAccessKey()
     */
    private final String accessKey;

    /**
     * @see GitHubRepositoryEntity#getWebHookSecret()
     */
    private final String webHookSecret;

    private GitHubRepositorySnapshotDto(Builder builder) {
        super(builder);

        username = builder.username;
        repository = builder.repository;
        accessKey = builder.accessKey;
        webHookSecret = builder.webHookSecret;
    }

    @Override
    public Type getType() {
        return Type.GIT_HUB;
    }

    /**
     * @see #accessKey
     */
    public Optional<String> getAccessKey() {
        return Optional.ofNullable(accessKey);
    }

    /**
     * @see #webHookSecret
     */
    public Optional<String> getWebHookSecret() {
        return Optional.ofNullable(webHookSecret);
    }

    /**
     * Builder of {@link GitHubRepositorySnapshotDto Git Hub repository snapshot}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder extends BaseGitRepositorySnapshotDto.Builder {

        private String username;
        private String repository;
        private String accessKey;
        private String webHookSecret;

        protected Builder() {
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder repository(String repository) {
            this.repository = repository;
            return this;
        }

        public Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder webHookSecret(String webHookSecret) {
            this.webHookSecret = webHookSecret;
            return this;
        }

        @Override
        public GitHubRepositorySnapshotDto build() {
            return new GitHubRepositorySnapshotDto(this);
        }
    }
}
