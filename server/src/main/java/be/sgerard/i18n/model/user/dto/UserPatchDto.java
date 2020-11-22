package be.sgerard.i18n.model.user.dto;

import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

/**
 * Patch update of a user (internal, or external).
 *
 * @author Sebastien Gerard
 */
@Schema(name = "UserPatch", description = "The update of a user.")
@JsonDeserialize(builder = UserPatchDto.Builder.class)
public class UserPatchDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "The new username.")
    private final String username;

    @Schema(description = "Name to display for this user (typically: first and last name).")
    private final String displayName;

    @Schema(description = "The new email address.")
    private final String email;

    @Schema(description = "The new password.")
    private final String password;

    @Schema(description = "The roles.")
    private final Collection<UserRole> roles;

    private UserPatchDto(Builder builder) {
        roles = builder.roles;
        username = builder.username;
        displayName = builder.displayName;
        email = builder.email;
        password = builder.password;
    }

    /**
     * Returns the new username.
     */
    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    /**
     * Returns the name to display for this user.
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * Returns the new email address.
     */
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    /**
     * Returns the new password.
     */
    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    /**
     * Returns the {@link UserRole#isAssignableByEndUser() assignable} new roles.
     */
    public Optional<Collection<UserRole>> getRoles() {
        return Optional.ofNullable(roles);
    }

    /**
     * Builder of {@link UserPatchDto user patch}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String username;
        private String displayName;
        private String email;
        private String password;
        private Collection<UserRole> roles;

        private Builder() {
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        @JsonProperty("roles")
        public Builder roles(Collection<UserRole> roles) {
            this.roles = (roles != null) ? unmodifiableCollection(new HashSet<>(roles)) : null;
            return this;
        }

        @JsonIgnore
        public Builder roles(UserRole... roles) {
            return roles(asList(roles));
        }

        public UserPatchDto build() {
            return new UserPatchDto(this);
        }
    }
}
