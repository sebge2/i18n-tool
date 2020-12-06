package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.user.dto.RepositoryRolesDto;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.security.repository.RepositoryCredentialsManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Mapper of {@link AuthenticatedUserDto user dto}.
 *
 * @author Sebastien Gerard
 */
@Component
public class AuthenticatedUserDtoMapper {

    private final RepositoryCredentialsManager credentialsManager;
    private final RepositoryManager repositoryManager;

    public AuthenticatedUserDtoMapper(RepositoryCredentialsManager credentialsManager, RepositoryManager repositoryManager) {
        this.credentialsManager = credentialsManager;
        this.repositoryManager = repositoryManager;
    }

    /**
     * Maps the {@link AuthenticatedUser authenticated user} to its {@link AuthenticatedUserDto DTO} representation.
     */
    public Mono<AuthenticatedUserDto> map(AuthenticatedUser authenticatedUser) {
        return repositoryManager
                .findAll()
                .flatMap(repository -> credentialsManager.loadUserCredentials(repository, authenticatedUser))
                .map(credentials -> RepositoryRolesDto.builder(credentials).build())
                .collectList()
                .map(roles ->
                        AuthenticatedUserDto
                                .builder()
                                .id(authenticatedUser.getId())
                                .userId(authenticatedUser.getUserId())
                                .sessionRoles(authenticatedUser.getRoles())
                                .repositoryRoles(roles)
                                .build()
                );
    }

}
