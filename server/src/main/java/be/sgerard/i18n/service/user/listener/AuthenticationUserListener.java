package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link UserListener User listener} impacting {@link be.sgerard.i18n.model.security.auth.AuthenticatedUser authentication}:
 * <ul>
 *     <li>If the user is removed, all his authentications are removed.</li>
 *     <li>If user change, all his authentications are updated accordingly.</li>
 * </ul>
 *
 * @author Sebastien Gerard
 */
@Component
public class AuthenticationUserListener implements UserListener {

    private final AuthenticationUserManager authenticationUserManager;

    public AuthenticationUserListener(AuthenticationUserManager authenticationUserManager) {
        this.authenticationUserManager = authenticationUserManager;
    }

    @Override
    public Mono<Void> afterUpdate(UserEntity user) {
        return authenticationUserManager.updateAll(user.getId(), user.getRoles());
    }

    @Override
    public Mono<Void> afterDelete(UserEntity user) {
        return authenticationUserManager.deleteAll(user.getId());
    }
}
