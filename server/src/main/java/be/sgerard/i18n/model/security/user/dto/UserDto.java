package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;
import java.util.Objects;

/**
 * User registered in the application, it can be external (from OAUTH), or internal.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "User", description = "Description of the user.")
@JsonDeserialize(builder = UserDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class UserDto {

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
                .type(userDto.type)
                .externalAuthSystem(userDto.externalAuthSystem);
    }

    public static Builder builder(UserEntity userEntity) {
        return builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .displayName(userEntity.getDisplayName())
                .email(userEntity.getEmail())
                .roles(userEntity.getRoles())
                .type(userEntity instanceof ExternalUserEntity ? Type.EXTERNAL : Type.INTERNAL)
                .externalAuthSystem(userEntity instanceof ExternalUserEntity ? ((ExternalUserEntity) userEntity).getExternalAuthSystem() : null);
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
    @Singular
    private final Collection<UserRole> roles;

    @Schema(description = "User type.")
    private final Type type;

    @Schema(description = "External system that authenticated the user.")
    private final ExternalAuthSystem externalAuthSystem;

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
