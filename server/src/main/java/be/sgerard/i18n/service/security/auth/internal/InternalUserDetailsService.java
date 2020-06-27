package be.sgerard.i18n.service.security.auth.internal;

import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * {@link ReactiveUserDetailsService User details service} for internal users.
 *
 * @author Sebastien Gerard
 */
@Service
public class InternalUserDetailsService implements ReactiveUserDetailsService {

    private final AuthenticationManager authenticationManager;

    public InternalUserDetailsService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDetails> findByUsername(String username) {
        return authenticationManager
                .createAuthentication(username)
                .map(user -> user);
    }
}
