package be.sgerard.i18n.service.user;

import be.sgerard.i18n.model.user.ExternalUser;
import be.sgerard.i18n.model.user.dto.CurrentUserPasswordUpdateDto;
import be.sgerard.i18n.model.user.dto.CurrentUserPatchDto;
import be.sgerard.i18n.model.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.user.dto.UserPatchDto;
import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;

/**
 * Manager of {@link UserEntity users}.
 *
 * @author Sebastien Gerard
 */
public interface UserManager {

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
     * Creates a new, or updates the existing {@link ExternalUserEntity external user} based on the specified {@link ExternalUser info}.
     * <p>
     * If the user is not allowed to access the application, no value may be returned.
     */
    Mono<ExternalUserEntity> createOrUpdate(ExternalUser externalUser);

    /**
     * Updates the {@link UserEntity user} based on the specified {@link UserPatchDto info}.
     */
    Mono<UserEntity> update(UserEntity user);

    /**
     * Updates the {@link UserEntity user} based on the specified {@link UserPatchDto info}.
     */
    Mono<UserEntity> update(String id, UserPatchDto patch);

    /**
     * Updates the current authenticated {@link UserEntity user} based on the specified {@link CurrentUserPatchDto info}.
     */
    Mono<UserEntity> updateCurrent(String currentUserId, CurrentUserPatchDto patch);

    /**
     * Updates the {@link InternalUserEntity#getAvatar() avatar} of the current user.
     *
     * @param contentType can be <tt>null</tt>
     */
    Mono<UserEntity> updateUserAvatar(String currentUserId, InputStream avatar, String contentType);

    /**
     * Updates the {@link InternalUserEntity#getPassword() password} of the current user.
     */
    Mono<UserEntity> updateCurrentPassword(String currentUserId, CurrentUserPasswordUpdateDto update);

    /**
     * Deletes the {@link UserEntity user} having the specified id.
     */
    Mono<UserEntity> delete(String id);

    /**
     * Initializes the default admin user if no user has been defined yet.
     */
    @SuppressWarnings("unused")
    Mono<InternalUserEntity> initializeDefaultAdmin();
}
