package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
public class ExternalKeyAuthenticatedUser extends User implements AuthenticatedUser {

    private final String id;
    private final UserDto user;
    private final String gitHubToken;

    public ExternalKeyAuthenticatedUser(String id,
                                        UserDto user,
                                        String password,
                                        String gitHubToken,
                                        Collection<GrantedAuthority> authorities) {
        super(user.getId(), password, authorities);

        this.id = id;
        this.user = user;
        this.gitHubToken = gitHubToken;
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
    public Optional<String> getGitHubToken() {
        return Optional.ofNullable(gitHubToken);
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
    public ExternalOAuth2AuthenticatedUser updateSessionRoles(List<UserRole> roles) {
        return new ExternalOAuth2AuthenticatedUser(
                id,
                user,
                gitHubToken,
                roles.stream().map(UserRole::toAuthority).collect(toList())
        );
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

        final ExternalKeyAuthenticatedUser that = (ExternalKeyAuthenticatedUser) o;

        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }
}
