package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Collections.unmodifiableCollection;

/**
 * The current authenticated user.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "AuthenticatedUser", description = "Description of an authenticated user.")
@JsonDeserialize(builder = AuthenticatedUserDto.Builder.class)
@Getter
public class AuthenticatedUserDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AuthenticatedUser authenticatedUser) {
        return builder()
                .id(authenticatedUser.getId())
                .userId(authenticatedUser.getUserId())
                .sessionRoles(authenticatedUser.getSessionRoles());
    }

    @Schema(description = "Unique id of the authenticated user.")
    private final String id;

    @Schema(description = "Description of the user.")
    private final String userId;

    @Schema(description = "Roles allowed during this session.")
    private final Collection<UserRole> sessionRoles;

    private AuthenticatedUserDto(Builder builder) {
        id = builder.id;
        userId = builder.userId;
        sessionRoles = unmodifiableCollection(builder.sessionRoles);
    }

    /**
     * Builder of {@link AuthenticatedUserDto authenticated user}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String id;
        private String userId;
        private final Collection<UserRole> sessionRoles = new HashSet<>();

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
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
