package be.sgerard.i18n.model.security.user;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Collections.unmodifiableCollection;

/**
 * The current authenticated user.
 *
 * @author Sebastien Gerard
 */
@ApiModel(value = "AuthenticatedUser", description = "Description of an authenticated user.")
@JsonDeserialize(builder = AuthenticatedUserDto.Builder.class)
public class AuthenticatedUserDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AuthenticatedUser authenticatedUser) {
        return builder()
                .id(authenticatedUser.getId())
                .user(authenticatedUser.getUser())
                .sessionRoles(authenticatedUser.getSessionRoles());
    }

    @ApiModelProperty(notes = "Unique id of the authenticated user.")
    private final String id;

    @ApiModelProperty(notes = "Description of the user.")
    private final UserDto user;

    @ApiModelProperty(notes = "Roles allowed during this session.")
    private final Collection<UserRole> sessionRoles;

    private AuthenticatedUserDto(Builder builder) {
        id = builder.id;
        user = builder.user;
        sessionRoles = unmodifiableCollection(builder.sessionRoles);
    }

    /**
     * Returns the unique id of the authenticated user.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the {@link UserDto current user}.
     */
    public UserDto getUser() {
        return user;
    }

    /**
     * Returns the current session security roles.
     */
    public Collection<UserRole> getSessionRoles() {
        return sessionRoles;
    }

    /**
     * Builder of {@link AuthenticatedUserDto authenticated user}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String id;
        private UserDto user;
        private final Collection<UserRole> sessionRoles = new HashSet<>();

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder user(UserDto user) {
            this.user = user;
            return this;
        }

        public Builder sessionRoles(Collection<UserRole> sessionRoles) {
            this.sessionRoles.addAll(sessionRoles);
            return this;
        }

        public AuthenticatedUserDto build() {
            return new AuthenticatedUserDto(this);
        }
    }
}
