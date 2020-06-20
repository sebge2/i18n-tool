package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * {@link UserDto User} authenticated, or about to be authenticated on the platform.
 *
 * @author Sebastien Gerard
 */
public interface AuthenticatedUser extends AuthenticatedPrincipal, Serializable {

    /**
     * Returns the unique id of the authenticated user, different from the user's id. Only related to the current
     * authentication.
     */
    String getId();

    /**
     * Returns the associated {@link UserDto user}.
     */
    UserDto getUser();

    /**
     * Returns the {@link UserRole roles} attributed to the user in this session.
     *
     * @see #getAuthorities()
     */
    Collection<UserRole> getSessionRoles();

    /**
     * Returns all the {@link GrantedAuthority authorities} of this user.
     *
     * @see #getSessionRoles()
     * @see UserRole#toAuthority()
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * Returns the {@link RepositoryCredentials credentials} to use for the specified repository.
     */
    <A extends RepositoryCredentials> Optional<A> getCredentials(String repository, Class<A> expectedType);

    /**
     * Updates {@link #getSessionRoles() session roles}.
     */
    AuthenticatedUser updateSessionRoles(List<UserRole> sessionRoles);
}
