package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.session.data.mongo.MongoSession;

import java.util.Optional;

import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

/**
 * Bunch of utility methods for authentication.
 *
 * @author Sebastien Gerard
 */
public final class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    /**
     * Creates the authentication for the specified internal user.
     */
    public static UsernamePasswordAuthenticationToken createAuthentication(InternalUserDetails userDetails,
                                                                           AuthenticatedUser authenticatedUser) {
        return new UsernamePasswordAuthenticationToken(authenticatedUser, userDetails.getPassword(), userDetails.getAuthorities());
    }

    /**
     * Creates the authentication for the specified external user.
     */
    public static OAuth2AuthenticationToken createAuthentication(ExternalUserDetails userDetails,
                                                                 ExternalAuthenticatedUser authenticatedUser) {
        return new OAuth2AuthenticationToken(authenticatedUser, userDetails.getAuthorities(), authenticatedUser.getToken().getExternalSystem().getName());
    }

    /**
     * Returns the {@link AuthenticatedUser authenticated user} from the specified {@link Authentication authentication}.
     */
    static Optional<AuthenticatedUser> getAuthenticatedUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof AuthenticatedUser) {
            return Optional.of((AuthenticatedUser) authentication.getPrincipal());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns the {@link AuthenticatedUser authenticated user} from the specified {@link MongoSession session}.
     */
    static Optional<AuthenticatedUser> getAuthenticatedUser(MongoSession session) {
        return Optional
                .ofNullable(session.getAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME))
                .map(SecurityContext.class::cast)
                .map(context -> getAuthenticatedUser(context.getAuthentication()))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    /**
     * Updates the {@link AuthenticatedUser authenticated user} associated to the specified {@link MongoSession session}.
     */
    static MongoSession setAuthenticatedUser(AuthenticatedUser authenticatedUser, MongoSession session) {
        return Optional
                .ofNullable(session.getAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME))
                .map(SecurityContext.class::cast)
                .map(securityContext -> {
                    securityContext.setAuthentication(updateAuthentication(authenticatedUser, securityContext.getAuthentication()));

                    return session;
                })
                .orElseThrow(() -> new IllegalStateException("Cannot retrieve security context from session."));
    }

    /**
     * Updates the {@link AuthenticatedUser authenticated user} associated to the specified {@link Authentication authentication}.
     */
    private static Authentication updateAuthentication(AuthenticatedUser authenticatedUser, Authentication authentication) {
        if ((authentication instanceof UsernamePasswordAuthenticationToken) && (authenticatedUser instanceof InternalAuthenticatedUser)) {
            final UsernamePasswordAuthenticationToken passwordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;

            return new UsernamePasswordAuthenticationToken(
                    authenticatedUser,
                    passwordAuthenticationToken.getCredentials(),
                    passwordAuthenticationToken.getAuthorities()
            );
        } else if ((authentication instanceof OAuth2AuthenticationToken) && (authenticatedUser instanceof ExternalAuthenticatedUser)) {
            final OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

            return new OAuth2AuthenticationToken(
                    (ExternalAuthenticatedUser) authenticatedUser,
                    oAuth2AuthenticationToken.getAuthorities(),
                    oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
            );
        } else {
            throw new UnsupportedOperationException("Unsupported authentication [" + authentication + "].");
        }
    }
}
