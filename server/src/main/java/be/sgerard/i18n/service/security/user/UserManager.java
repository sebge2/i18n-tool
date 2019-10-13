package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.security.user.ExternalUserDto;
import be.sgerard.i18n.model.security.user.InternalUserEntity;
import be.sgerard.i18n.model.security.user.UserEntity;

import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface UserManager {

    Optional<UserEntity> getUserById(String id);

    Optional<InternalUserEntity> getUserByName(String username);

    UserEntity createOrUpdateUser(ExternalUserDto externalUser);

}
