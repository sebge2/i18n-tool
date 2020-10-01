package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.security.auth.external.RawExternalUser;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * {@link DefaultOAuth2UserService OAauth2 user service} creating the {@link ExternalAuthenticatedUser external authenticated user}.
 *
 * @author Sebastien Gerard
 */
@Service
public class ExternalUserDetailsService extends DefaultReactiveOAuth2UserService {

    private final ExternalUserExtractor externalUserHandler;
    private final UserManager userManager;
    private final AuthenticationUserManager authenticationUserManager;

    public ExternalUserDetailsService(ExternalUserExtractor externalUserHandler,
                                      UserManager userManager,
                                      AuthenticationUserManager authenticationUserManager) {
        this.externalUserHandler = externalUserHandler;
        this.userManager = userManager;
        this.authenticationUserManager = authenticationUserManager;
    }

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return super
                .loadUser(userRequest)
                .flatMap(oAuth2User ->
                        Mono.just(createOauthUser(userRequest, oAuth2User))
                                .flatMap(rawExternalUser ->
                                        externalUserHandler
                                                .map(rawExternalUser)
                                                .flatMap(userManager::createOrUpdate)
                                                .map(externalUser -> new ExternalUserDetails(externalUser, rawExternalUser.getAttributes(), rawExternalUser.getToken()))
                                                .flatMap(authenticationUserManager::createUser)
                                                .map(OAuth2User.class::cast)
                                                .switchIfEmpty(Mono.just(oAuth2User))
                                )
                );
    }

    /**
     * Creates the {@link RawExternalUser external} representation of a user.
     */
    private RawExternalUser createOauthUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        return new RawExternalUser(
                ExternalAuthSystem.fromName(userRequest.getClientRegistration().getClientName()),
                userRequest.getAccessToken().getTokenValue(),
                oAuth2User.getAttributes()
        );
    }
}
