package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;

/**
 * Roles that has an authenticated user on a repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "RepositoryRoles", description = "Roles that has an authenticated user on a repository.")
@JsonDeserialize(builder = RepositoryRolesDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class RepositoryRolesDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(RepositoryCredentials repositoryCredentials) {
        return builder()
                .repository(repositoryCredentials.getRepository())
                .sessionRoles(repositoryCredentials.getSessionRoles());
    }

    @Schema(description = "Unique id of the associated repository.")
    private final String repository;

    @Schema(description = "All the roles that the user has on this repository.")
    @Singular
    private final Collection<UserRole> sessionRoles;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {
    }
}
