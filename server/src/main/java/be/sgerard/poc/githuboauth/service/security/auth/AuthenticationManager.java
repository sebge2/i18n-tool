package be.sgerard.poc.githuboauth.service.security.auth;

import be.sgerard.poc.githuboauth.model.security.user.ExternalUserDto;
import be.sgerard.poc.githuboauth.model.security.user.UserEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    Optional<UserEntity> getCurrentUser();

    Optional<UserEntity> getUserById(String id);

    UserEntity createOrUpdateUser(ExternalUserDto externalUser);

    String getAuthToken() throws AccessDeniedException;

    boolean isAuthenticated();

    Collection<String> getCurrentUserRoles() throws AccessDeniedException;
}
