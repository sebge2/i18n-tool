package be.sgerard.i18n.model.security.auth.internal;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * {@link AuthenticatedUser Authenticated internal user}.
 *
 * @author Sebastien Gerard
 */
public class InternalAuthenticatedUser implements AuthenticatedUser, UserDetails, CredentialsContainer {

    private final String id;
    private final UserDto user;
    private final Set<UserRole> roles;

    private String password;

    public InternalAuthenticatedUser(String id,
                                     UserDto user,
                                     String password,
                                     Collection<UserRole> roles) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.roles = Set.copyOf(roles);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public UserDto getUser() {
        return user;
    }

    @Override
    public String getName() {
        return getUser().getId();
    }

    @Override
    public Collection<UserRole> getSessionRoles() {
        return roles;
    }

    @Override
    public InternalAuthenticatedUser updateSessionRoles(List<UserRole> sessionRoles) {
        return new InternalAuthenticatedUser(
                id,
                user,
                password,
                sessionRoles
        );
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return mapToAuthorities(getSessionRoles());
    }

    @Override
    public <A extends RepositoryCredentials> Optional<A> getCredentials(String repository, Class<A> expectedType) {
        return Optional.empty();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return getName();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) return false;

        final InternalAuthenticatedUser that = (InternalAuthenticatedUser) o;

        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }

    /**
     * Maps the specified roles to authorities.
     */
    private static Set<GrantedAuthority> mapToAuthorities(Collection<UserRole> roles) {
        return Stream
                .concat(
                        Stream.of(ROLE_USER),
                        roles.stream().map(UserRole::toAuthority)
                )
                .collect(toSet());
    }
}
