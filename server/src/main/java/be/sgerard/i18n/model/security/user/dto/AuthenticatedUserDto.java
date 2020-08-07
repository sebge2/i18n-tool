package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

/**
 * The current authenticated user.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "AuthenticatedUser", description = "Description of an authenticated user.")
@JsonDeserialize(builder = AuthenticatedUserDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class AuthenticatedUserDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AuthenticatedUser authenticatedUser) {
        return builder()
                .id(authenticatedUser.getId())
                .userId(authenticatedUser.getUserId())
                .sessionRoles(authenticatedUser.getSessionRoles())
                .repositoryRoles(
                        authenticatedUser.getRepositoryCredentials().stream()
                                .map(cred -> RepositoryRolesDto.builder(cred).build())
                                .collect(toList())
                );
    }

    @Schema(description = "Unique id of the authenticated user.")
    private final String id;

    @Schema(description = "Description of the user.")
    private final String userId;

    @Schema(description = "Roles allowed during this session.")
    @Singular
    private final Collection<UserRole> sessionRoles;

    @Schema(description = "All the current repository roles.")
    @Singular
    private final Collection<RepositoryRolesDto> repositoryRoles;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
