package be.sgerard.i18n.model.security.auth.internal;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * {@link AuthenticatedUser Authenticated internal user}.
 *
 * @author Sebastien Gerard
 */
public class InternalAuthenticatedUser implements AuthenticatedUser, UserDetails, CredentialsContainer {

    private final String id;
    private final UserDto user;
    private final Set<GrantedAuthority> authorities;
    private final Map<String, RepositoryCredentials> repositoryAuthentications;

    private String password;

    public InternalAuthenticatedUser(String id,
                                     UserDto user,
                                     String password,
                                     Collection<GrantedAuthority> authorities,
                                     Collection<RepositoryCredentials> repositoryCredentials) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.authorities = new HashSet<>(authorities);
        this.repositoryAuthentications = repositoryCredentials.stream().collect(toMap(RepositoryCredentials::getRepository, auth -> auth));
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
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(UserRole.ROLE_PREFIX))
                .map(authority -> authority.substring(UserRole.ROLE_PREFIX.length()))
                .map(UserRole::valueOf)
                .collect(toList());
    }

    @Override
    public InternalAuthenticatedUser updateSessionRoles(List<UserRole> sessionRoles) {
        return new InternalAuthenticatedUser(
                id,
                user,
                password,
                sessionRoles.stream().map(UserRole::toAuthority).collect(toSet()),
                repositoryAuthentications.values()
        );
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public <A extends RepositoryCredentials> Optional<A> getCredentials(String repository, Class<A> expectedType) {
        return Optional.ofNullable(repositoryAuthentications.get(repository))
                .map(expectedType::cast);
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
}
