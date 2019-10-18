package be.sgerard.i18n.model.security.user;

import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public class ExternalUserDto {

    public static Builder builder() {
        return new Builder();
    }

    private final String externalId;
    private final String username;
    private final String email;
    private final String avatarUrl;
    private final String gitHubToken;

    private ExternalUserDto(Builder builder) {
        externalId = builder.externalId;
        username = builder.username;
        email = builder.email;
        avatarUrl = builder.avatarUrl;
        gitHubToken = builder.gitHubToken;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Optional<String> getGitHubToken() {
        return Optional.ofNullable(gitHubToken);
    }

    @Override
    public String toString() {
        return "ExternalUser(" + username + ", " + email + ')';
    }

    public static final class Builder {

        private String externalId;
        private String username;
        private String email;
        private String avatarUrl;
        private String gitHubToken;

        private Builder() {
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder gitHubToken(String gitHubToken) {
            this.gitHubToken = gitHubToken;
            return this;
        }

        public ExternalUserDto build() {
            return new ExternalUserDto(this);
        }
    }
}
