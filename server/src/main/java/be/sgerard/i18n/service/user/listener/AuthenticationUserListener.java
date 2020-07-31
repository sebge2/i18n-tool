package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
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

    private final AuthenticationManager authenticationManager;

    public AuthenticationUserListener(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> onUpdate(UserEntity user) {
        return authenticationManager.updateAuthentications(user);
    }

    @Override
    public Mono<Void> onDelete(UserEntity user) {
        return authenticationManager.deleteAllAuthentications(user.getId());
    }
}
