package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link UserListener User listener} emitting events notifying updates about users.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserEventListener implements UserListener {

    private final EventService eventService;

    public UserEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Mono<Void> onCreate(UserEntity user) {
        return eventService.sendEventToUsers(UserRole.ADMIN, EventType.UPDATED_USER, UserDto.builder(user).build());
    }

    @Override
    public Mono<Void> onUpdate(UserEntity user) {
        final UserDto updatedUserDto = UserDto.builder(user).build();
        return Mono
                .zip(
                        eventService.sendEventToUsers(UserRole.ADMIN, EventType.UPDATED_USER, updatedUserDto),
                        eventService.sendEventToUser(updatedUserDto, EventType.UPDATED_CURRENT_USER, updatedUserDto)
                )
                .then();
    }

    @Override
    public Mono<Void> onDelete(UserEntity user) {
        return eventService
                .sendEventToUsers(UserRole.ADMIN, EventType.DELETED_USER, UserDto.builder(user).build())
                .then();
    }
}
