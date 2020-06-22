package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

/**
 * User registered in the application, it can be external (from an OAUTH), or internal.
 *
 * @author Sebastien Gerard
 */
@ApiModel(value = "User", description = "Description of the user.")
@JsonDeserialize(builder = UserDto.Builder.class)
public class UserDto implements Principal, Serializable {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UserDto userDto) {
        return builder()
                .id(userDto.id)
                .username(userDto.username)
                .email(userDto.email)
                .avatarUrl(userDto.avatarUrl)
                .roles(userDto.roles)
                .type(userDto.type);
    }

    public static Builder builder(UserEntity userEntity) {
        return builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .avatarUrl(userEntity.getAvatarUrl())
                .roles(userEntity.getRoles())
                .type(userEntity instanceof ExternalUserEntity ? Type.EXTERNAL : Type.INTERNAL);
    }

    @ApiModelProperty(notes = "Id of the user.")
    private final String id;

    @ApiModelProperty(notes = "Username of the user.")
    private final String username;

    @ApiModelProperty(notes = "Email of the user.")
    private final String email;

    @ApiModelProperty(notes = "URL of the user avatar.")
    private final String avatarUrl;

    @ApiModelProperty(notes = "User roles.")
    private final Collection<UserRole> roles;

    @ApiModelProperty(notes = "User type.")
    private final Type type;

    private UserDto(Builder builder) {
        id = builder.id;
        username = builder.username;
        email = builder.email;
        avatarUrl = builder.avatarUrl;
        roles = unmodifiableSet(builder.roles);
        type = builder.type;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return getId();
    }

    /**
     * Returns the unique id of this user.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the unique username.
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
     * Returns the URL of the user avatar..
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Returns the {@link UserRole#isAssignableByEndUser() assignable} roles.
     */
    public Collection<UserRole> getRoles() {
        return roles;
    }

    /**
     * Returns the {@link Type type} of user.
     */
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "User(" + username + ", " + email + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UserDto userDto = (UserDto) o;

        return id.equals(userDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Builder of {@link UserDto user}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String id;
        private String username;
        private String email;
        private String avatarUrl;
        private final Set<UserRole> roles = new HashSet<>();
        private Type type;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
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

        @JsonProperty("roles")
        public Builder roles(Collection<UserRole> roles) {
            this.roles.addAll(roles);
            return this;
        }

        @JsonIgnore
        public Builder roles(UserRole... roles) {
            return roles(asList(roles));
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public UserDto build() {
            return new UserDto(this);
        }
    }

    /**
     * All possible user types.
     */
    @ApiModel(description = "All possible user types.")
    public enum Type {

        EXTERNAL,

        INTERNAL

    }
}
