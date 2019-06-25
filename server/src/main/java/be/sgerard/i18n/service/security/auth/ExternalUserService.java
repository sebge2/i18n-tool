package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.ExternalKeyAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.ExternalOAuth2AuthenticatedUser;
import be.sgerard.i18n.model.security.user.ExternalUserDto;
import be.sgerard.i18n.model.security.user.ExternalUserEntity;
import be.sgerard.i18n.service.security.user.UserManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@Service
public class ExternalUserService extends DefaultOAuth2UserService implements UserDetailsService {

    public static final String USER_PREFIX = "#";

    private final UserManager userManager;
    private final AuthenticationManager authenticationManager;
    private final List<ExternalUserExtractor> handlers;
    private final ExternalGitHubUserExtractor gitHubUserExtractor;

    public ExternalUserService(UserManager userManager,
                               AuthenticationManager authenticationManager,
                               List<ExternalUserExtractor> handlers,
                               ExternalGitHubUserExtractor gitHubUserExtractor) {
        this.userManager = userManager;
        this.authenticationManager = authenticationManager;
        this.handlers = handlers;
        this.gitHubUserExtractor = gitHubUserExtractor;
    }

    @Override
    @Transactional
    public ExternalOAuth2AuthenticatedUser loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);

        final ExternalUserDto externalUserDto = handlers.stream()
                .filter(handler -> handler.support(userRequest))
                .findFirst()
                .map(handler -> handler.loadUser(userRequest, oAuth2User))
                .orElseThrow(() -> new IllegalStateException("There is no handler able to handle the current request." +
                        " Hint: check that all handlers have been registered."));

        final ExternalUserEntity currentUser = userManager.createOrUpdateUser(externalUserDto);

        return authenticationManager.initExternalOAuthUser(currentUser, externalUserDto);
    }

    @Override
    @Transactional
    public ExternalKeyAuthenticatedUser loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!username.startsWith(USER_PREFIX)) {
            throw new UsernameNotFoundException("There is no user name [" + username + "].");
        }

        final ExternalUserDto externalUserDto = gitHubUserExtractor.loadUser(username.substring(USER_PREFIX.length()));

        final ExternalUserEntity currentUser = userManager.createOrUpdateUser(externalUserDto);

        return authenticationManager.initExternalKeyUser(currentUser, externalUserDto);
    }
}
