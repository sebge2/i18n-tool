package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.ExternalKeyAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.ExternalOAuth2AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.ExternalUserDto;
import be.sgerard.i18n.model.security.user.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.InternalUserEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    ExternalOAuth2AuthenticatedUser initExternalOAuthUser(ExternalUserEntity currentUser, ExternalUserDto externalUserDto);

    ExternalKeyAuthenticatedUser initExternalKeyUser(ExternalUserEntity currentUser, ExternalUserDto externalUserDto);

    InternalAuthenticatedUser initInternalUser(InternalUserEntity currentUser);

    Optional<AuthenticatedUser> getCurrentUser();

    default AuthenticatedUser getCurrentUserOrFail() throws AccessDeniedException {
        return getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Please authenticate."));
    }

}
