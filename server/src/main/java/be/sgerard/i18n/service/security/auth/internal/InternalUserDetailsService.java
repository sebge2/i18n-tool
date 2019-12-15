package be.sgerard.i18n.service.security.auth.internal;

import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import be.sgerard.i18n.service.user.UserManager;
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

    private final UserManager userManager;

    public InternalUserDetailsService(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDetails> findByUsername(String username) {
        return userManager
                .finUserByNameOrDie(username)
                .map(InternalUserDetails::new);
    }
}
