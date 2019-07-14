package be.sgerard.poc.githuboauth.configuration;

import be.sgerard.poc.githuboauth.service.auth.AuthenticationManager;
import be.sgerard.poc.githuboauth.service.auth.GitHubAuthoritiesExtractor;
import be.sgerard.poc.githuboauth.service.auth.GitHubPrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;

/**
 * @author Sebastien Gerard
 */
@Configuration
@EnableOAuth2Sso
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String ROLE_REPO_MEMBER = "REPO_MEMBER";

    public static final String ROLE_USER = "USER";

    private final OAuth2ClientContext context;
    private final AppProperties appProperties;
    private final AuthenticationManager authenticationManager;

    public SecurityConfiguration(OAuth2ClientContext context,
                                 AppProperties appProperties,
                                 AuthenticationManager authenticationManager) {
        this.context = context;
        this.appProperties = appProperties;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public AuthoritiesExtractor authoritiesExtractor() {
        return new GitHubAuthoritiesExtractor(context, appProperties);
    }

    @Bean
    public PrincipalExtractor principalExtractor() {
        return new GitHubPrincipalExtractor(authenticationManager);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/login**", "/api/authentication/authenticated", "/api/git-hub/**")
                .permitAll()
                .anyRequest()
                .hasAnyAuthority(ROLE_REPO_MEMBER, ROLE_USER)
                .and().csrf().disable();
    }

}
