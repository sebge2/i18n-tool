package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.ExternalOAuth2AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

/**
 * Manager of authentication.
 *
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    ExternalOAuth2AuthenticatedUser initExternalOAuthUser(ExternalUserEntity currentUser, ExternalUserDto externalUserDto);

    InternalAuthenticatedUser initInternalUser(InternalUserEntity currentUser);

    /**
     * Returns the current {@link AuthenticatedUser authenticated user}.
     */
    Optional<AuthenticatedUser> getCurrentUser();

    /**
     * Returns the current {@link AuthenticatedUser authenticated user}.
     */
    default AuthenticatedUser getCurrentUserOrDie() throws AccessDeniedException {
        return getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Please authenticate."));
    }

}
