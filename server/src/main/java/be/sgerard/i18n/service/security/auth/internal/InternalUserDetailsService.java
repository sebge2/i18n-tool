package be.sgerard.i18n.service.security.auth.internal;

import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link UserDetailsService User details service} for internal users.
 *
 * @author Sebastien Gerard
 */
@Service
public class InternalUserDetailsService implements UserDetailsService {

    private final AuthenticationManager authenticationManager;

    public InternalUserDetailsService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authenticationManager.createAuthentication(username).block();
    }

}
