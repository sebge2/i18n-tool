package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.support.StringUtils;
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

    @Schema(description = "The unique name of this repository.")
    private final String name;

    @Schema(description = "Username to use to connect to the Git repository (empty means that it will be removed)")
    private final String username;

    @Schema(description = "Password to connect to the Git repository (empty means that it will be removed)")
    private final String password;

    protected GitRepositoryPatchDto(Builder builder) {
        super(builder);

        this.name = builder.name;
        this.username = builder.username;
        this.password = builder.password;
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
     * @see #username
     */
    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    /**
     * Returns the username to use after patching the specified repository.
     */
    public Optional<String> getUpdatedUserName(GitRepositoryEntity repository) {
        return getUsername().or(repository::getUsername).filter(StringUtils::isNotEmptyString);
    }

    /**
     * @see #password
     */
    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    /**
     * Returns the user's password to use after patching the specified repository.
     */
    public Optional<String> getUpdatedPassword(GitRepositoryEntity repository) {
        return getPassword().or(repository::getPassword).filter(StringUtils::isNotEmptyString);
    }

    /**
     * Builder of {@link GitRepositoryPatchDto GIT repository patch DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder extends BaseBuilder<GitRepositoryPatchDto, GitRepositoryPatchDto.Builder> {

        private String name;
        private String username;
        private String password;

        public Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
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
        public GitRepositoryPatchDto build() {
            return new GitRepositoryPatchDto(this);
        }
    }
}
