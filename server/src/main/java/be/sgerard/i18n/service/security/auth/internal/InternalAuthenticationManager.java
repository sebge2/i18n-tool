package be.sgerard.i18n.service.security.auth.internal;

import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.createAuthentication;

/**
 * {@link UserDetailsRepositoryReactiveAuthenticationManager Authentication manager} for internal users.
 * <p>
 * Unfortunately, Spring creates the principal before user's credentials are validated. This class helps to create
 * the authentication with the right principal once we know that the user is allowed to access the app.
 *
 * @author Sebastien Gerard
 */
public class InternalAuthenticationManager extends UserDetailsRepositoryReactiveAuthenticationManager {

    private final AuthenticationUserManager authenticationUserManager;

    public InternalAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                         AuthenticationUserManager authenticationUserManager) {
        super(userDetailsService);

        this.authenticationUserManager = authenticationUserManager;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return super
                .authenticate(authentication)
                .map(auth -> (InternalUserDetails) auth.getPrincipal())
                .flatMap(userDetails ->
                        authenticationUserManager
                                .createUser(userDetails)
                                .map(authenticatedUser -> createAuthentication(userDetails, authenticatedUser))
                );
    }
}
