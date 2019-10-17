package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.user.UserEntity;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    Optional<UserEntity> getCurrentUser();

    UserEntity getCurrentUserOrFail() throws AccessDeniedException;

    UserEntity getUserFromPrincipal(Principal principal);

    String getGitHubToken() throws AccessDeniedException;

    Collection<String> getCurrentUserRoles() throws AccessDeniedException;
}
