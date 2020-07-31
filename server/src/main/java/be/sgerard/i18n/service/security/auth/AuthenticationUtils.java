package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.data.mongo.MongoSession;

import java.util.Optional;

import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

/**
 * Bunch of utility methods for authentication.
 *
 * @author Sebastien Gerard
 */
final class AuthenticationUtils {

    private AuthenticationUtils() {
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
            return new UsernamePasswordAuthenticationToken(
                    authenticatedUser,
                    ((InternalAuthenticatedUser) authenticatedUser).getPassword(),
                    authenticatedUser.getAuthorities()
            );
        } else {
            throw new UnsupportedOperationException("Unsupported authentication [" + authentication + "].");
        }
    }

}
