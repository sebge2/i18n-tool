package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * {@link UserListener User listener} impacting {@link be.sgerard.i18n.model.security.auth.AuthenticatedUser authication}:
 * <ul>
 *     <li>If the user is removed, all his authentication are removed.</li>
 *     <li>If user's roles change, all his authentication are updated accordingly.</li>
 * </ul>
 *
 * @author Sebastien Gerard
 */
@Component
public class SessionUserListener implements UserListener {

    private final AuthenticationManager authenticationManager;

    public SessionUserListener(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> onUpdate(UserEntity user) {
        return authenticationManager
                .findAll(user.getId())
                .map(authenticatedUser -> authenticatedUser.updateSessionRoles(
                        Stream
                                .concat(
                                        authenticatedUser.getSessionRoles().stream().filter(role -> !role.isAssignableByEndUser()),
                                        user.getRoles().stream()
                                )
                                .collect(toList())
                ))
                .flatMap(authenticationManager::update)
                .then();
    }

    @Override
    public Mono<Void> onDelete(UserEntity user) {
        return authenticationManager
                .findAll(user.getId())
                .flatMap(authenticationManager::delete)
                .then();
    }
}
