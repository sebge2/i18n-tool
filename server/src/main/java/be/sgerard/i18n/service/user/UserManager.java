package be.sgerard.i18n.service.user;

import be.sgerard.i18n.model.security.user.*;
import be.sgerard.i18n.service.ResourceNotFoundException;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface UserManager {

    /**
     * User name of admin.
     */
    String ADMIN_USER_NAME = "admin";

    Optional<UserEntity> getUserById(String id);

    default UserEntity getUserByIdOrFail(String id) {
        return getUserById(id)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(id));
    }

    Collection<UserEntity> getAllUsers();

    Optional<InternalUserEntity> getUserByName(String username);

    InternalUserEntity createUser(UserCreationDto info);

    ExternalUserEntity createOrUpdateUser(ExternalUserDto externalUser);

    UserEntity updateUser(String id, UserPatchDto userUpdate);

    void deleteUserById(String id);

}
