package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.user.ExternalUserDto;
import be.sgerard.i18n.model.security.user.ExternalUserEntity;
import be.sgerard.i18n.service.security.user.UserManager;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@Service
public class ExternalOAuthUserService extends DefaultOAuth2UserService {

    private final UserManager userManager;
    private final AuthenticationManager authenticationManager;
    private final List<ExternalUserExtractor> handlers;

    public ExternalOAuthUserService(UserManager userManager,
                                    AuthenticationManager authenticationManager,
                                    List<ExternalUserExtractor> handlers) {
        this.userManager = userManager;
        this.authenticationManager = authenticationManager;
        this.handlers = handlers;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);

        final ExternalUserDto externalUserDto = handlers.stream()
                .filter(handler -> handler.support(userRequest))
                .findFirst()
                .map(handler -> handler.loadUser(userRequest, oAuth2User))
                .orElseThrow(() -> new IllegalStateException("There is no handler able to handle the current request." +
                        " Hint: check that all handlers have been registered."));

        final ExternalUserEntity currentUser = userManager.createOrUpdateUser(externalUserDto);

        return authenticationManager.loadAuthenticatedUser(currentUser, externalUserDto);
    }
}
