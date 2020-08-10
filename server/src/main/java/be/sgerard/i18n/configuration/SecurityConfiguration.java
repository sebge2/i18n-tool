package be.sgerard.i18n.configuration;

import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.security.auth.external.ExternalUserDetailsService;
import be.sgerard.i18n.service.security.auth.external.OAuthUserMapper;
import be.sgerard.i18n.service.security.auth.internal.InternalAuthenticationManager;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.session.data.mongo.config.annotation.web.reactive.EnableMongoWebSession;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Security configuration.
 *
 * @author Sebastien Gerard
 */
@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@EnableMongoWebSession
public class SecurityConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final ReactiveUserDetailsService internalUserDetailsService;
    private final AuthenticationUserManager authenticationUserManager;
    private final OAuthUserMapper externalUserHandler;
    private final UserManager userManager;
    private final ReactiveClientRegistrationRepository repository;

    public SecurityConfiguration(PasswordEncoder passwordEncoder,
                                 ReactiveUserDetailsService internalUserDetailsService,
                                 AuthenticationUserManager authenticationUserManager,
                                 OAuthUserMapper externalUserHandler,
                                 UserManager userManager,
                                 @Autowired(required = false) ReactiveClientRegistrationRepository repository) {
        this.passwordEncoder = passwordEncoder;

        this.internalUserDetailsService = internalUserDetailsService;
        this.authenticationUserManager = authenticationUserManager;
        this.externalUserHandler = externalUserHandler;
        this.userManager = userManager;
        this.repository = repository;
    }

    @Bean
    @Primary
    public ReactiveAuthenticationManager authenticationManager(List<ReactiveAuthenticationManager> delegates) {
        return new DelegatingReactiveAuthenticationManager(delegates);
    }

    @Bean
    public ReactiveAuthenticationManager internalAuthenticationManager() {
        final InternalAuthenticationManager internalAuthenticationManager =
                new InternalAuthenticationManager(internalUserDetailsService, authenticationUserManager);

        internalAuthenticationManager.setPasswordEncoder(passwordEncoder);

        return internalAuthenticationManager;
    }

    @Bean
    public OAuth2LoginReactiveAuthenticationManager externalAuthenticationManager() {
        return new OAuth2LoginReactiveAuthenticationManager(
                new WebClientReactiveAuthorizationCodeTokenResponseClient(),
                reactiveOAuth2UserService()
        );
    }

    @Bean
    public ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> reactiveOAuth2UserService() {
        return new ExternalUserDetailsService(externalUserHandler, userManager, authenticationUserManager);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        final ServerHttpSecurity httpSecurity = http
                .authorizeExchange()
                .pathMatchers("/", "/**", "/auth/**", "/api/authentication/**", "/api/git-hub/**").permitAll()
                .anyExchange().hasAnyRole(UserRole.MEMBER_OF_ORGANIZATION.name()).and()

                .logout()
                .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/auth/logout"))
                .logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler(HttpStatus.OK))
                .and()

                .csrf().disable()

                .httpBasic()
                .authenticationManager(internalAuthenticationManager())
                .authenticationEntryPoint((exchange, e) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return Mono.empty();
                })
                .and()

                .securityContextRepository(new WebSessionServerSecurityContextRepository())

                .formLogin(formLogin ->
                        formLogin
                                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/login"))
                                .requiresAuthenticationMatcher(exchange -> ServerWebExchangeMatcher.MatchResult.notMatch())
                                .authenticationFailureHandler((webFilterExchange, exception) -> {
                                    webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                    return Mono.empty();
                                })
                );


        if (repository != null) {
            // TODO unfortunately oauth login redirect to /login if not authenticated
            httpSecurity.oauth2Login(oAuth2Login ->
                    oAuth2Login
                            .authorizationRequestResolver(new DefaultServerOAuth2AuthorizationRequestResolver(repository, ServerWebExchangeMatchers.pathMatchers(("/auth/oauth2/authorize-client/{registrationId}"))))
                            .authenticationMatcher(new PathPatternParserServerWebExchangeMatcher("/auth/oauth2/code/{registrationId}"))
                            .authenticationManager(externalAuthenticationManager())
            );
        }

        return httpSecurity.build();
    }

}
