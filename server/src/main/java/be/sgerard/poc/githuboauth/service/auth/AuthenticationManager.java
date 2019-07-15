package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.model.auth.ExternalUserDto;
import be.sgerard.poc.githuboauth.model.auth.UserEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    UserEntity getCurrentUser() throws AccessDeniedException;

    Optional<UserEntity> getUserById(String id);

    UserEntity createOrUpdateUser(ExternalUserDto externalUser);

    String getAuthToken() throws AccessDeniedException;

    boolean isAuthenticated();

    Collection<String> getCurrentUserRoles() throws AccessDeniedException;
}
