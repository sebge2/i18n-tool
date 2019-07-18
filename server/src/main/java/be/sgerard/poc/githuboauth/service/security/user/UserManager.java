package be.sgerard.poc.githuboauth.service.security.user;

import be.sgerard.poc.githuboauth.model.security.user.ExternalUserDto;
import be.sgerard.poc.githuboauth.model.security.user.UserEntity;

import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface UserManager {

    Optional<UserEntity> getUserById(String id);

    UserEntity createOrUpdateUser(ExternalUserDto externalUser);

}
