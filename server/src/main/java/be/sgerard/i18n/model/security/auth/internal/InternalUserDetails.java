package be.sgerard.i18n.model.security.auth.internal;

import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
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

    public InternalUserEntity getInternalUser() {
        return internalUser;
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
}
