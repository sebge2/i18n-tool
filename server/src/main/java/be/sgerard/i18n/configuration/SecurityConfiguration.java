package be.sgerard.i18n.configuration;

import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.GitHubAuthoritiesExtractor;
import be.sgerard.i18n.service.security.auth.GitHubPrincipalExtractor;
import be.sgerard.i18n.service.security.user.UserManager;
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

    private final OAuth2ClientContext context;
    private final AppProperties appProperties;
    private final UserManager userManager;

    public SecurityConfiguration(OAuth2ClientContext context,
                                 AppProperties appProperties,
                                 UserManager userManager) {
        this.context = context;
        this.appProperties = appProperties;
        this.userManager = userManager;
    }

    @Bean
    public AuthoritiesExtractor authoritiesExtractor() {
        return new GitHubAuthoritiesExtractor(context, appProperties);
    }

    @Bean
    public PrincipalExtractor principalExtractor() {
        return new GitHubPrincipalExtractor(userManager);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/*",
                        "/login",
                        "/api/authentication/authenticated",
                        "/api/authentication/user",
                        "/api/git-hub/**"
                )
                .permitAll()
                .anyRequest()
                .hasAnyAuthority(UserRole.REPO_MEMBER.name(), UserRole.USER.name())
                .and().logout().logoutSuccessUrl("/logout/success").permitAll()
                .and().csrf().disable();
    }

}
