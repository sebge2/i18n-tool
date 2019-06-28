package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.configuration.AppProperties;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2ClientContext;

import java.util.List;
import java.util.Map;

import static be.sgerard.poc.githuboauth.configuration.UiSecurityConfiguration.ROLE_REPO_MEMBER;

/**
 * @author Sebastien Gerard
 */
public class GitHubAuthoritiesExtractor extends FixedAuthoritiesExtractor {

    private final OAuth2ClientContext context;
    private final String repository;

    public GitHubAuthoritiesExtractor(OAuth2ClientContext context, AppProperties appProperties) {
        this.context = context;
        this.repository = appProperties.getRepoFqnName();
    }

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        final List<GrantedAuthority> authorities = super.extractAuthorities(map);

        if (isRepoMember()) {
            authorities.add(new SimpleGrantedAuthority(ROLE_REPO_MEMBER));
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
