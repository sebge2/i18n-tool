package be.sgerard.i18n.model.security.auth.internal;

import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static be.sgerard.i18n.service.security.UserRole.mapToAuthorities;

/**
 * Expose the {@link UserDetails details} of an {@link InternalUserEntity internal user}
 *
 * @author Sebastien Gerard
 */
public class InternalUserDetails implements UserDetails {

    private final InternalUserEntity internalUser;

    public InternalUserDetails(InternalUserEntity internalUser) {
        this.internalUser = internalUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return mapToAuthorities(internalUser.getRoles());
    }

    @Override
    public String getPassword() {
        return internalUser.getPassword();
    }

    @Override
    public String getUsername() {
        return internalUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns the unique id of the associated user.
     */
    public String getId() {
        return internalUser.getId();
    }

    /**
     * Returns the user's roles.
     */
    public Collection<UserRole> getRoles() {
        return internalUser.getRoles();
    }

    /**
     * Returns the name to be displayed to the end-user (ideally composed of the first name, last name).
     */
    public String getDisplayName() {
        return internalUser.getDisplayName();
    }

    /**
     * Returns the user's email.
     */
    public String getEmail() {
        return internalUser.getEmail();
    }
}
