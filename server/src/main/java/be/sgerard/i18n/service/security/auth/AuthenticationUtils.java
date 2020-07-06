package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.security.Principal;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public final class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    // TODO
    public static AuthenticatedUser getAuthenticatedUserOrFail(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken && ((OAuth2AuthenticationToken) principal).getPrincipal() instanceof AuthenticatedUser) {
            return (AuthenticatedUser) ((OAuth2AuthenticationToken) principal).getPrincipal();
        } else if (principal instanceof UsernamePasswordAuthenticationToken) {
            return (AuthenticatedUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        } else if (principal instanceof AuthenticatedUser) {
            return (AuthenticatedUser) principal;
        } else {
            throw new IllegalArgumentException("The principal is not of the expected format. " +
                    "Hint: are you sure the authentication is valid?");
        }
    }

    static Optional<AuthenticatedUser> getAuthenticatedUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof AuthenticatedUser) {
            return Optional.of((AuthenticatedUser) authentication.getPrincipal());
        } else {
            return Optional.empty();
        }
    }

    static Authentication updateAuthentication(Authentication authentication, AuthenticatedUser authenticatedUser) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            return new OAuth2AuthenticationToken(
                    (OAuth2User) authenticatedUser,
                    authenticatedUser.getAuthorities(),
                    ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId()
            );
        } else if ((authentication instanceof UsernamePasswordAuthenticationToken) && (authenticatedUser instanceof InternalAuthenticatedUser)) {
            return new UsernamePasswordAuthenticationToken(
                    authenticatedUser,
                    ((InternalAuthenticatedUser) authenticatedUser).getPassword(),
                    authenticatedUser.getAuthorities()
            );
        } else if ((authentication instanceof UsernamePasswordAuthenticationToken) && (authenticatedUser instanceof ExternalAuthenticatedUser)) {
            return new UsernamePasswordAuthenticationToken(
                    authenticatedUser,
                    null,
                    authenticatedUser.getAuthorities()
            );
        } else if (authentication instanceof AnonymousAuthenticationToken) {
            return authentication;
        } else {
            throw new UnsupportedOperationException("Unsupported authentication [" + authentication + "].");
        }
    }
}
