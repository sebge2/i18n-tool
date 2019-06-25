package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
public final class ExternalOAuth2AuthenticatedUser extends DefaultOAuth2User implements AuthenticatedUser {

    public static final String NAME_ATTRIBUTE = "principal_id";

    private final String id;
    private final UserDto user;
    private final String gitHubToken;

    public ExternalOAuth2AuthenticatedUser(String id,
                                           UserDto user,
                                           String gitHubToken,
                                           Collection<GrantedAuthority> authorities) {
        super(authorities, singletonMap(NAME_ATTRIBUTE, user.getId()), NAME_ATTRIBUTE);

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

        final ExternalOAuth2AuthenticatedUser that = (ExternalOAuth2AuthenticatedUser) o;

        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }
}
