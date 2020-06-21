package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.service.security.UserRole;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * User authenticated externally.
 *
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
    private final Collection<UserRole> roles;

    private ExternalUserDto(Builder builder) {
        externalId = builder.externalId;
        username = builder.username;
        email = builder.email;
        avatarUrl = builder.avatarUrl;
        roles = Collections.unmodifiableSet(builder.roles);
    }

    /**
     * Returns the unique id of the user in the external system.
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the URL of the user's avatar.
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Returns {@link UserRole roles} that the user has.
     */
    public Collection<UserRole> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "ExternalUser(" + username + ", " + email + ')';
    }

    /**
     * Builder of {@link ExternalUserDto external user}.
     */
    public static final class Builder {

        private String externalId;
        private String username;
        private String email;
        private String avatarUrl;
        private final Set<UserRole> roles = new HashSet<>();

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

        public Builder roles(UserRole... roles) {
            this.roles.addAll(asList(roles));
            return this;
        }

        public ExternalUserDto build() {
            return new ExternalUserDto(this);
        }
    }
}
