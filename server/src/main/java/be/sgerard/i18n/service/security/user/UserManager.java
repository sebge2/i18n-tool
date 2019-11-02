package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.security.user.*;
import be.sgerard.i18n.service.ResourceNotFoundException;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface UserManager {

    Optional<UserEntity> getUserById(String id);

    default UserEntity getUserByIdOrFail(String id) {
        return getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with id [" + id + "]."));
    }

    Collection<UserEntity> loadAllUsers();

    Optional<InternalUserEntity> getUserByName(String username);

    InternalUserEntity createUser(UserCreationDto info);

    ExternalUserEntity createOrUpdateUser(ExternalUserDto externalUser);

    UserEntity updateUser(String id, UserUpdateDto userUpdate);

    void deleteUserById(String id);

}
