package be.sgerard.i18n.service.user;

import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
import be.sgerard.i18n.model.security.user.dto.UserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of {@link UserEntity users}.
 *
 * @author Sebastien Gerard
 */
public interface UserManager {

    /**
     * User name of admin.
     */
    String ADMIN_USER_NAME = "admin";

    /**
     * Finds the {@link UserEntity user} having the specified id.
     */
    Mono<UserEntity> getUserById(String id);

    /**
     * Finds the {@link UserEntity user} having the specified {@link UserEntity#getId() id}.
     */
    default Mono<UserEntity> getUserByIdOrFail(String id) {
        return getUserById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.workspaceNotFoundException(id)));
    }

    /**
     * Returns all the {@link UserEntity users}.
     */
    Flux<UserEntity> getAllUsers();

    /**
     * Finds the {@link UserEntity user} having the specified {@link UserEntity#getUsername() username}.
     */
    Mono<InternalUserEntity> getUserByName(String username);

    /**
     * Creates a new {@link InternalUserEntity internal user} based on the specified {@link UserCreationDto info}.
     */
    Mono<InternalUserEntity> createUser(UserCreationDto info);

    /**
     * Creates a new, or updates the existing {@link ExternalUserEntity external user} based on the specified {@link ExternalUserDto info}.
     */
    Mono<ExternalUserEntity> createOrUpdateUser(ExternalUserDto externalUser);

    /**
     * Updates the {@link InternalUserEntity internal user} based on the specified {@link UserPatchDto info}.
     */
    Mono<UserEntity> updateUser(String id, UserPatchDto patch);

    /**
     * Deletes the {@link UserEntity user} having the specified id.
     */
    Mono<UserEntity> deleteUserById(String id);

}
