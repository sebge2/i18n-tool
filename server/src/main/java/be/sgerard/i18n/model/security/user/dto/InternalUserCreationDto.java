package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;

/**
 * DTO asking the creation of an internal user.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "InternalUserCreation", description = "The initial information needed to create an internal user.")
@JsonDeserialize(builder = InternalUserCreationDto.Builder.class)
@Getter
public class InternalUserCreationDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UserDto userDto) {
        return builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .roles(userDto.getRoles());
    }

    @Schema(description = "Username used to log in.", required = true)
    private final String username;

    @Schema(description = "User email address", required = true)
    private final String email;

    @Schema(description = "User password (non encoded)", required = true)
    private final String password;

    @Schema(description = "Assignable user roles", required = true)
    private final Collection<UserRole> roles;

    private InternalUserCreationDto(Builder builder) {
        roles = builder.roles;
        username = builder.username;
        email = builder.email;
        password = builder.password;
    }

    /**
     * Builder of {@link InternalUserCreationDto internal user creation DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String username;
        private String email;
        private String password;
        private final Collection<UserRole> roles = new HashSet<>();

        private Builder() {
        }

        public Builder username(String username) {
            this.username = username;
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
            this.roles.addAll(roles);
            return this;
        }

        @JsonIgnore
        public Builder roles(UserRole... roles) {
            return roles(asList(roles));
        }

        public InternalUserCreationDto build() {
            return new InternalUserCreationDto(this);
        }
    }
}
