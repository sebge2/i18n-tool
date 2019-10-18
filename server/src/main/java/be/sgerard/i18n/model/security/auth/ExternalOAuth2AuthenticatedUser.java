package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
public final class ExternalOAuth2AuthenticatedUser extends DefaultOAuth2User implements AuthenticatedUser {

    public static final String NAME_ATTRIBUTE = "principal_id";

    private final String userId;
    private final String gitHubToken;

    public ExternalOAuth2AuthenticatedUser(String userId,
                                           String gitHubToken,
                                           Collection<GrantedAuthority> authorities) {
        super(authorities, Collections.singletonMap(NAME_ATTRIBUTE, userId), NAME_ATTRIBUTE);

        this.userId = userId;
        this.gitHubToken = gitHubToken;
    }

    @Override
    public String getUserId() {
        return userId;
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
