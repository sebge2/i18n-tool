package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.user.InternalUserEntity;
import be.sgerard.i18n.service.security.user.UserManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sebastien Gerard
 */
@Service
public class InternalUserDetailsService implements UserDetailsService {

    private final UserManager userManager;
    private final AuthenticationManager authenticationManager;

    public InternalUserDetailsService(UserManager userManager, AuthenticationManager authenticationManager) {
        this.userManager = userManager;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final InternalUserEntity user = userManager
                .getUserByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("There is no user [" + username + "]."));

        return authenticationManager.initInternalUser(user);
    }

}
