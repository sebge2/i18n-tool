package be.sgerard.i18n.service.user;

import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
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
    Mono<UserEntity> findById(String id);

    /**
     * Finds the {@link UserEntity user} having the specified {@link UserEntity#getId() id}.
     */
    default Mono<UserEntity> findByIdOrDie(String id) {
        return findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.userNotFoundException(id)));
    }

    /**
     * Finds the {@link UserEntity user} having the specified {@link UserEntity#getUsername() username}.
     */
    Mono<InternalUserEntity> finUserByName(String username);

    /**
     * Finds the {@link UserEntity user} having the specified {@link UserEntity#getUsername() username}.
     */
    default Mono<InternalUserEntity> finUserByNameOrDie(String username) {
        return finUserByName(username)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.userNotFoundException(username)));
    }

    /**
     * Returns all the {@link UserEntity users}.
     */
    Flux<UserEntity> findAll();

    /**
     * Creates a new {@link InternalUserEntity internal user} based on the specified {@link InternalUserCreationDto info}.
     */
    Mono<InternalUserEntity> createUser(InternalUserCreationDto info);

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
    Mono<UserEntity> delete(String id);

}
