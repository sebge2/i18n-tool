package be.sgerard.i18n.service.security.auth.listener;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.AuthenticatedUserDto;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.event.EventType.*;

/**
 * {@link AuthenticatedUserListener Listener} of authenticated user that emits events accordingly.
 *
 * @author Sebastien Gerard
 */
@Component
public class EventAuthenticatedUserListener implements AuthenticatedUserListener {

    private final EventService eventService;

    public EventAuthenticatedUserListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Mono<Void> onUpdate(AuthenticatedUser authenticatedUser) {
        final AuthenticatedUserDto dto = AuthenticatedUserDto.builder(authenticatedUser).build();

        return Mono
                .zip(
                        eventService.sendEventToUser(authenticatedUser, UPDATED_CURRENT_AUTHENTICATED_USER, dto),
                        eventService.sendEventToUsers(UserRole.ADMIN, UPDATED_AUTHENTICATED_USER, dto)
                )
                .then();
    }

    @Override
    public Mono<Void> onDelete(AuthenticatedUser authenticatedUser) {
        final AuthenticatedUserDto dto = AuthenticatedUserDto.builder(authenticatedUser).build();

        return Mono
                .zip(
                        eventService.sendEventToUser(authenticatedUser, DELETED_CURRENT_AUTHENTICATED_USER, dto),
                        eventService.sendEventToUsers(UserRole.ADMIN, DELETED_AUTHENTICATED_USER, AuthenticatedUserDto.builder(authenticatedUser).build())
                )
                .then();
    }
}
