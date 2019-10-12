package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.service.security.UserRole;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2ClientContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sebastien Gerard
 */
public class GitHubAuthoritiesExtractor implements AuthoritiesExtractor {

    private final OAuth2ClientContext context;
    private final String repository;

    public GitHubAuthoritiesExtractor(OAuth2ClientContext context, AppProperties appProperties) {
        this.context = context;
        this.repository = appProperties.getRepoFqnName();
    }

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        final List<GrantedAuthority> authorities = new ArrayList<>();

        if (isRepoMember()) {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.name()));
            authorities.add(new SimpleGrantedAuthority(UserRole.REPO_MEMBER.name()));
        }

        return authorities;
    }

    private boolean isRepoMember() {
        final RtGithub github = new RtGithub(context.getAccessToken().getValue());

        try {
            return github.repos().get(new Coordinates.Simple(repository)).branches().iterate().iterator().hasNext();
        } catch (AssertionError e) {
            return false;
        }
    }

}
