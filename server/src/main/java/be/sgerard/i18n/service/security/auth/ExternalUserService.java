package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.user.persistence.ExternalAuthSystem;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link DefaultOAuth2UserService OAauth2 user service} creating the {@link ExternalAuthenticatedUser external authenticated user}.
 *
 * @author Sebastien Gerard
 */
@Service
public class ExternalUserService extends DefaultOAuth2UserService {

    private final AuthenticationManager authenticationManager;

    public ExternalUserService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public ExternalAuthenticatedUser loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);

        return authenticationManager
                .createAuthentication(
                        new OAuthExternalUser(
                                ExternalAuthSystem.fromName(userRequest.getClientRegistration().getClientName()),
                                userRequest.getAccessToken().getTokenValue(),
                                oAuth2User.getAttributes()
                        )
                )
                .block();
    }
}
