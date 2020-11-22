package be.sgerard.i18n.model.user.snapshot;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Snapshot of a {@link UserEntity user}.
 *
 * @author Sebastien Gerard
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InternalUserSnapshotDto.class, name = "INTERNAL"),
        @JsonSubTypes.Type(value = ExternalUserSnapshotDto.class, name = "EXTERNAL")
})
public abstract class UserSnapshotDto {

    /**
     * @see UserEntity#getId()
     */
    private final String id;

    /**
     * @see UserEntity#getUsername()
     */
    private final String username;

    /**
     * @see UserEntity#getDisplayName()
     */
    private final String displayName;

    /**
     * @see UserEntity#getEmail()
     */
    private final String email;

    /**
     * @see UserEntity#getRoles()
     */
    private final Set<UserRole> roles;

    /**
     * @see UserEntity#getPreferences()
     */
    private final UserPreferencesSnapshotDto preferences;

    protected UserSnapshotDto(Builder builder) {
        id = builder.id;
        username = builder.username;
        displayName = builder.displayName;
        email = builder.email;
        roles = unmodifiableSet(builder.roles);
        preferences = builder.preferences;
    }

    /**
     * Returns the {@link Type type} of this user.
     */
    public abstract Type getType();

    /**
     * Builder of {@link UserSnapshotDto user}.
     */
    public static abstract class Builder {

        private String id;
        private String username;
        private String displayName;
        private String email;
        private Set<UserRole> roles = new HashSet<>();
        private UserPreferencesSnapshotDto preferences;

        protected Builder() {
        }

        public abstract UserSnapshotDto build();

        public Builder id(String id) {
            this.id = id;
            return this;
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

        public Builder roles(Set<UserRole> roles) {
            this.roles = roles;
            return this;
        }

        public Builder preferences(UserPreferencesSnapshotDto preferences) {
            this.preferences = preferences;
            return this;
        }
    }

    /**
     * All possible user types.
     */
    public enum Type {

        /**
         * @see be.sgerard.i18n.model.user.persistence.InternalUserEntity
         */
        EXTERNAL,

        /**
         * @see be.sgerard.i18n.model.user.persistence.ExternalUserEntity
         */
        INTERNAL

    }

    /**
     * @see be.sgerard.i18n.service.security.UserRole
     */
    public enum UserRole {

        /**
         * @see be.sgerard.i18n.service.security.UserRole#MEMBER_OF_ORGANIZATION
         */
        MEMBER_OF_ORGANIZATION,

        /**
         * @see be.sgerard.i18n.service.security.UserRole#MEMBER_OF_REPOSITORY
         */
        MEMBER_OF_REPOSITORY,

        /**
         * @see be.sgerard.i18n.service.security.UserRole#ADMIN
         */
        ADMIN
    }

}
