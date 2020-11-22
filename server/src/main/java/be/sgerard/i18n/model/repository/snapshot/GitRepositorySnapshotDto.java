package be.sgerard.i18n.model.repository.snapshot;

import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import java.util.Optional;

/**
 * Snapshot of a {@link GitRepositoryEntity Git Hub repository}.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = GitRepositorySnapshotDto.Builder.class)
@Getter
public class GitRepositorySnapshotDto extends BaseGitRepositorySnapshotDto {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * @see GitRepositoryEntity#getUsername()
     */
    private final String username;

    /**
     * @see GitRepositoryEntity#getPassword()
     */
    private final String password;

    private GitRepositorySnapshotDto(Builder builder) {
        super(builder);

        username = builder.username;
        password = builder.password;
    }

    @Override
    public Type getType() {
        return Type.GIT;
    }

    /**
     * @see #username
     */
    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    /**
     * @see #password
     */
    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    /**
     * Builder of {@link GitRepositorySnapshotDto Git repository snapshot}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder extends BaseGitRepositorySnapshotDto.Builder {

        private String username;
        private String password;

        protected Builder() {
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        @Override
        public GitRepositorySnapshotDto build() {
            return new GitRepositorySnapshotDto(this);
        }
    }
}
