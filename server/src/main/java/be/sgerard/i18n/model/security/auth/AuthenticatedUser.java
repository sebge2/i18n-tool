package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toSet;

/**
 * @author Sebastien Gerard
 */
public interface AuthenticatedUser extends AuthenticatedPrincipal, Serializable {

    String getId();

    UserDto getUser();

    Optional<String> getGitHubToken();

    default String getGitHubTokenOrFail() {
        return getGitHubToken()
                .orElseThrow(() -> new AccessDeniedException("Cannot access to GitHub"));
    }

    Collection<UserRole> getSessionRoles();

    Collection<? extends GrantedAuthority> getAuthorities();

    AuthenticatedUser updateSessionRoles(List<UserRole> roles);
}
