package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
    public Mono<Void> beforeUpdate(UserEntity user) {
        return updateAll(user.getId(), user.getRoles());
    }

    @Override
    public Mono<Void> beforeDelete(UserEntity user) {
        return authenticationUserManager.deleteAll(user.getId());
    }

    /**
     * Updates roles of all the {@link AuthenticatedUser users} linked to the specified {@link UserEntity user}.
     *
     * @see UserEntity#getId()
     */
    private Mono<Void> updateAll(String userId, Collection<UserRole> roles) {
        return authenticationUserManager
                .findAll(userId)
                .map(authenticatedUser -> authenticatedUser.updateRoles(
                        Stream
                                .concat(
                                        authenticatedUser.getRoles().stream().filter(role -> !role.isAssignableByEndUser()),
                                        roles.stream()
                                )
                                .collect(toList())
                ))
                .flatMap(authenticationUserManager::update)
                .then();
    }
}
