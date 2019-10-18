package be.sgerard.i18n.model.security.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public final class ExternalOAuth2User extends DefaultOAuth2User {

    public static final String NAME_ATTRIBUTE = "principal_id";

    private final String gitHubToken;

    public ExternalOAuth2User(String principalId,
                              String gitHubToken,
                              Collection<GrantedAuthority> authorities) {
        super(authorities, Collections.singletonMap(NAME_ATTRIBUTE, principalId), NAME_ATTRIBUTE);

        this.gitHubToken = gitHubToken;
    }

    public Optional<String> getGitHubToken() {
        return Optional.ofNullable(gitHubToken);
    }

}
