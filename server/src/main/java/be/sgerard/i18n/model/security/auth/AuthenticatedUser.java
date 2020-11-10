package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Returns the {@link UserRole roles} attributed to the user.
     *
     * @see #getAuthorities()
     */
    Collection<UserRole> getRoles();

    /**
     * Returns all the {@link GrantedAuthority authorities} of this user.
     *
     * @see #getRoles()
     * @see UserRole#toAuthority()
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * Returns the name to be displayed to the end-user (ideally composed of the first name, last name).
     */
    String getDisplayName();

    /**
     * Returns the user's email.
     */
    String getEmail();

    /**
     * Returns the context associated to this user.
     */
    Map<String, Object> getContext();

    /**
     * Returns the value in the context associated to this user.
     */
    <V> Optional<V> getContextValue(String key, Class<V> valueType);

    /**
     * Updates the current context (if the key exists, it's overridden). The context will be persisted
     * and easily accessible during call processing.
     */
    AuthenticatedUser updateContext(String key, Object value);

    /**
     * Bulk update of the user's context.
     */
    default AuthenticatedUser updateContext(Map<String, Object> context) {
        context.forEach(this::updateContext);
        return this;
    }

    /**
     * Updates {@link #getRoles() roles}.
     */
    AuthenticatedUser updateRoles(List<UserRole> sessionRoles);

}
