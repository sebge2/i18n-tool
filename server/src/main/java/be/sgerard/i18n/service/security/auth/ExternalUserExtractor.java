package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * @author Sebastien Gerard
 */
public interface ExternalUserExtractor {

    boolean support(OAuth2UserRequest request);

    ExternalUserDto loadUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) throws BadCredentialsException;
}
