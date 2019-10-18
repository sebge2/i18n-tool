package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface AuthenticatedUser extends AuthenticatedPrincipal {

    String getUserId();

    Optional<String> getGitHubToken();

    default String getGitHubTokenOrFail() {
        return getGitHubToken()
                .orElseThrow(() -> new AccessDeniedException("Cannot access to GitHub"));
    }

    Collection<UserRole> getRoles();

}
