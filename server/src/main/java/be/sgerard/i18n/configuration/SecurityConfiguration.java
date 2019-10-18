package be.sgerard.i18n.configuration;

import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.ExternalOAuthUserService;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Sebastien Gerard
 */
@Configuration
@EnableOAuth2Sso
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ExternalOAuthUserService externalOAuthUserService;
    private final UserDetailsService userDetailsService;

    public SecurityConfiguration(PasswordEncoder passwordEncoder,
                                 ExternalOAuthUserService externalOAuthUserService,
                                 UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.externalOAuthUserService = externalOAuthUserService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        return daoAuthenticationProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/*",
                        "/auth/**",
                        "/api/authentication/authenticated",
                        "/api/authentication/user"
                )
                .permitAll()
                .anyRequest()
                .hasAnyRole(UserRole.MEMBER_OF_ORGANIZATION.name())

                .and().logout().logoutSuccessUrl("/logout/success").permitAll().and()

                .csrf().disable()

                .httpBasic().realmName("I18n Tool").and()

                .oauth2Login()
                .authorizationEndpoint().baseUri("/auth/oauth2/authorize-client").and()
                .redirectionEndpoint().baseUri("/auth/oauth2/code/*").and()

                .userInfoEndpoint().userService(externalOAuthUserService);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(daoAuthenticationProvider())
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
