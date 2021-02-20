package be.sgerard.i18n.service.security.auth.listener;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.AuthenticatedUserDtoMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.event.EventType.*;

/**
 * {@link AuthenticatedUserListener Listener} of authenticated user that emits events accordingly.
 *
 * @author Sebastien Gerard
 */
@Component
public class AuthenticatedUserEventListener implements AuthenticatedUserListener {

    private final EventService eventService;
    private final AuthenticatedUserDtoMapper userDtoMapper;

    public AuthenticatedUserEventListener(EventService eventService, AuthenticatedUserDtoMapper userDtoMapper) {
        this.eventService = eventService;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public Mono<Void> afterUpdate(AuthenticatedUser authenticatedUser) {
        return userDtoMapper
                .map(authenticatedUser)
                .flatMap(dto ->
                        Mono
                                .zip(
                                        eventService.sendEventToUser(authenticatedUser, UPDATED_CURRENT_AUTHENTICATED_USER, dto),
                                        eventService.sendEventToUsers(UserRole.ADMIN, UPDATED_AUTHENTICATED_USER, dto)
                                )
                                .then()
                );
    }

    @Override
    public Mono<Void> afterDelete(AuthenticatedUser authenticatedUser) {
        return userDtoMapper
                .map(authenticatedUser)
                .flatMap(dto ->
                        Mono
                                .zip(
                                        eventService.sendEventToUser(authenticatedUser, DELETED_CURRENT_AUTHENTICATED_USER, dto),
                                        eventService.sendEventToUsers(UserRole.ADMIN, DELETED_AUTHENTICATED_USER, dto)
                                )
                                .then()
                );
    }
}
