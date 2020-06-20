package be.sgerard.i18n.configuration;

import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.ExternalUserService;
import be.sgerard.i18n.service.security.auth.InternalUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import javax.servlet.http.HttpServletResponse;

/**
 * Security configuration.
 *
 * @author Sebastien Gerard
 */
@Configuration
@EnableOAuth2Client
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ExternalUserService externalUserService;
    private final InternalUserDetailsService internalUserDetailsService;

    public SecurityConfiguration(PasswordEncoder passwordEncoder,
                                 ExternalUserService externalUserService,
                                 InternalUserDetailsService internalUserDetailsService) {
        this.passwordEncoder = passwordEncoder;

        this.externalUserService = externalUserService;
        this.internalUserDetailsService = internalUserDetailsService;
    }

    @Bean
    public AuthenticationProvider internalUserAuthenticationProvider() {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(internalUserDetailsService);

        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationProvider externalUserAuthenticationProvider() {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(externalUserService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

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
                        "/ws/*",
                        "/auth/**",
                        "/api/authentication/authenticated",
                        "/api/authentication/user",
                        "/api/git-hub/**"
                )
                .permitAll()
                .anyRequest()
                .hasAnyRole(UserRole.MEMBER_OF_ORGANIZATION.name())

                .and().logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> httpServletResponse.setStatus(HttpServletResponse.SC_OK))
                .permitAll().and()

                .csrf().disable()

                .httpBasic().realmName("I18n Tool").and()

                .oauth2Login()
                .authorizationEndpoint().baseUri("/auth/oauth2/authorize-client").and()
                .redirectionEndpoint().baseUri("/auth/oauth2/code/*").and()

                .userInfoEndpoint().userService(externalUserService);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(internalUserAuthenticationProvider());
        auth.authenticationProvider(externalUserAuthenticationProvider());
    }
}
