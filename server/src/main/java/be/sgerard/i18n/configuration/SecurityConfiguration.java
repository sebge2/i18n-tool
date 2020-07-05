package be.sgerard.i18n.configuration;

import be.sgerard.i18n.service.security.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
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
    private final ReactiveClientRegistrationRepository repository;

    public SecurityConfiguration(PasswordEncoder passwordEncoder,
                                 ReactiveUserDetailsService internalUserDetailsService, ReactiveClientRegistrationRepository repository) {
        this.passwordEncoder = passwordEncoder;

        this.internalUserDetailsService = internalUserDetailsService;
        this.repository = repository;
    }

    @Bean
    public ReactiveAuthenticationManager internalAuthenticationManager() {
        final UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(internalUserDetailsService);

        authenticationManager.setPasswordEncoder(passwordEncoder);

        return authenticationManager;
    }

    @Bean
    @Primary
    public ReactiveAuthenticationManager authenticationManager(List<ReactiveAuthenticationManager> delegates) {
        return new DelegatingReactiveAuthenticationManager(delegates);
    }

    @Autowired
    ReactiveClientRegistrationRepository clientRegistrationRepository;

//    @Bean
//    public ReactiveOAuth2UserService reactiveOAuth2UserService() {
//        return new ReactiveOAuth2UserService() {
//
//            @Override
//            public Mono loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//                System.out.println("SGE");
//                System.out.println("");
//                System.out.println("");
//                System.out.println("");
//                System.out.println("");
//
//                return Mono.empty();
//            }
//        };
//    }

    @Bean
    public OAuth2LoginReactiveAuthenticationManager manager() {
        return new OAuth2LoginReactiveAuthenticationManager(new WebClientReactiveAuthorizationCodeTokenResponseClient(),
                new OAuth2UserRequestOAuth2UserReactiveOAuth2UserService());
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .pathMatchers("/").permitAll()
                .pathMatchers("/*").permitAll()
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers("/api/authentication/**").permitAll()
                .pathMatchers("/api/git-hub/**").permitAll()
                .anyExchange().hasAnyRole(UserRole.MEMBER_OF_ORGANIZATION.name()).and()

                .logout()
                .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/auth/logout"))
                .logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler(HttpStatus.OK))
                .and()

                .csrf().disable()

                .httpBasic()
                .authenticationManager(internalAuthenticationManager())
                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/login"))
                .and()

                .securityContextRepository(new WebSessionServerSecurityContextRepository())


                .oauth2Login(new Customizer<ServerHttpSecurity.OAuth2LoginSpec>() {
                    @Override
                    public void customize(ServerHttpSecurity.OAuth2LoginSpec oAuth2LoginSpec) {
                        oAuth2LoginSpec
                                .authorizationRequestResolver(new DefaultServerOAuth2AuthorizationRequestResolver(repository, ServerWebExchangeMatchers.pathMatchers(("/auth/oauth2/authorize-client/{registrationId}"))));
                    }
                })

                // TODO login

                .build();

//                .anyExchange()
//
////                .antMatcher("/**")
////                .authorizeRequests()
//                .antMatchers(
//                        "/",
//                        "/*",
//                        "/ws/*",
//                        "/auth/**",
//                        "/api/authentication/authenticated",
//                        "/api/authentication/user",
//                        "/api/git-hub/**"
//                )
//                .permitAll()
//                .anyRequest()
//                .hasAnyRole(UserRole.MEMBER_OF_ORGANIZATION.name())
//
//                .and().logout()
//                .logoutUrl("/auth/logout")
//                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> httpServletResponse.setStatus(HttpServletResponse.SC_OK))
//                .permitAll().and()
//
//                .csrf().disable()
//
//                .httpBasic().realmName("I18n Tool").and()
//
//                .oauth2Login()
//                .authorizationEndpoint().baseUri("/auth/oauth2/authorize-client").and()
//                .redirectionEndpoint().baseUri("/auth/oauth2/code/*");
    }

    private static class OAuth2UserRequestOAuth2UserReactiveOAuth2UserService extends DefaultReactiveOAuth2UserService {
        @Override
        public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
            return super.loadUser(userRequest)

                    .doOnNext(user -> {
                        System.out.println(user);
                    });
        }
    }

}
