package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

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
@Schema(name = "User", description = "Description of the user.")
@JsonDeserialize(builder = UserDto.Builder.class)
@Getter
public class UserDto implements Principal, Serializable {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UserDto userDto) {
        return builder()
                .id(userDto.id)
                .username(userDto.username)
                .displayName(userDto.displayName)
                .email(userDto.email)
                .roles(userDto.roles)
                .type(userDto.type);
    }

    public static Builder builder(UserEntity userEntity) {
        return builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .displayName(userEntity.getDisplayName())
                .email(userEntity.getEmail())
                .roles(userEntity.getRoles())
                .type(userEntity instanceof ExternalUserEntity ? Type.EXTERNAL : Type.INTERNAL);
    }

    @Schema(description = "Id of the user.")
    private final String id;

    @Schema(description = "Username of the user.")
    private final String username;

    @Schema(description = "Name to display for this user (typically: first and last name).")
    private final String displayName;

    @Schema(description = "Email of the user.")
    private final String email;

    @Schema(description = "User roles.")
    private final Collection<UserRole> roles;

    @Schema(description = "User type.")
    private final Type type;

    private UserDto(Builder builder) {
        id = builder.id;
        username = builder.username;
        displayName = builder.displayName;
        email = builder.email;
        roles = unmodifiableSet(builder.roles);
        type = builder.type;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return getId();
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
        private String displayName;
        private String email;
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

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
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
    @Schema(description = "All possible user types.")
    public enum Type {

        EXTERNAL,

        INTERNAL

    }
}
