package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalUser;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * {@link DefaultOAuth2UserService OAauth2 user service} creating the {@link ExternalAuthenticatedUser external authenticated user}.
 *
 * @author Sebastien Gerard
 */
@Service
public class ExternalUserDetailsService extends DefaultReactiveOAuth2UserService {

    private final OAuthUserMapper externalUserHandler;
    private final UserManager userManager;
    private final AuthenticationUserManager authenticationUserManager;

    public ExternalUserDetailsService(OAuthUserMapper externalUserHandler, UserManager userManager, AuthenticationUserManager authenticationUserManager) {
        this.externalUserHandler = externalUserHandler;
        this.userManager = userManager;
        this.authenticationUserManager = authenticationUserManager;
    }

    @Override
    @Transactional
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return super
                .loadUser(userRequest)
                .map(oAuth2User -> createOauthUser(userRequest, oAuth2User))
                .flatMap(oauthUser ->
                        externalUserHandler
                                .map(oauthUser)
                                .flatMap(userManager::createOrUpdate)
                                .map(externalUser -> new ExternalUserDetails(externalUser, oauthUser.getAttributes(), oauthUser.getToken()))
                                .flatMap(authenticationUserManager::createUser)
                );
    }

    /**
     * Creates the {@link ExternalUser external} representation of a user.
     */
    private ExternalUser createOauthUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        return new ExternalUser(
                ExternalAuthSystem.fromName(userRequest.getClientRegistration().getClientName()),
                userRequest.getAccessToken().getTokenValue(),
                oAuth2User.getAttributes()
        );
    }
}
