package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static be.sgerard.i18n.service.security.UserRole.mapToAuthorities;

/**
 * {@link UserDto User} authenticated, or about to be authenticated on the platform.
 *
 * @author Sebastien Gerard
 */
public interface AuthenticatedUser extends AuthenticatedPrincipal, Serializable {

    /**
     * {@link GrantedAuthority Authority} that every user has.
     */
    GrantedAuthority ROLE_USER = new SimpleGrantedAuthority("ROLE_USER");

    /**
     * Returns the unique id of the authenticated user, different from the user's id. Only related to the current
     * authentication.
     */
    String getId();

    /**
     * Returns the id of the associated {@link be.sgerard.i18n.model.security.user.persistence.UserEntity user}.
     */
    String getUserId();

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
    default Collection<? extends GrantedAuthority> getAuthorities() {
        return mapToAuthorities(getSessionRoles());
    }

    /**
     * Returns the {@link RepositoryCredentials credentials} to use for the specified repository.
     */
    <A extends RepositoryCredentials> Optional<A> getCredentials(String repository, Class<A> expectedType);

    /**
     * Returns all the available {@link RepositoryCredentials credentials}.
     */
    Collection<RepositoryCredentials> getRepositoryCredentials();

    /**
     * Updates {@link #getSessionRoles() session roles}.
     */
    AuthenticatedUser updateSessionRoles(List<UserRole> sessionRoles);
}
