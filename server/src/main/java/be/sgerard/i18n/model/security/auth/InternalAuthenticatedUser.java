package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
public class InternalAuthenticatedUser extends User implements AuthenticatedUser {

    private final String userId;
    private final String gitHubToken;

    public InternalAuthenticatedUser(String userId,
                                     String username,
                                     String password,
                                     String gitHubToken,
                                     Collection<GrantedAuthority> authorities) {
        super(username, password, authorities);

        this.userId = userId;
        this.gitHubToken = gitHubToken;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return getUsername();
    }

    @Override
    public Optional<String> getGitHubToken() {
        return Optional.ofNullable(gitHubToken);
    }

    @Override
    public Collection<UserRole> getRoles() {
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(UserRole.ROLE_PREFIX))
                .map(authority -> authority.substring(UserRole.ROLE_PREFIX.length()))
                .map(UserRole::valueOf)
                .collect(toList());
    }
}
