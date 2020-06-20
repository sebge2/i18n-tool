package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.event.EventService;
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

    // TODO delete live sessions + translation modification author

    @Override
    public Mono<Void> onCreate(UserEntity user) {
        //        eventService.broadcastInternally(EventType.UPDATED_USER, UserDto.builder(userEntity).build());
//        eventService.sendEventToUsers(UserRole.ADMIN, EventType.UPDATED_USER, UserDto.builder(userEntity).build());

        return Mono.empty();
    }

    @Override
    public Mono<Void> onUpdate(UserEntity user) {
        //        eventService.broadcastInternally(EventType.UPDATED_USER, updatedUserDto);
//        eventService.sendEventToUsers(UserRole.ADMIN, EventType.UPDATED_USER, updatedUserDto);
//        eventService.sendEventToUser(updatedUserDto, EventType.UPDATED_CURRENT_USER, updatedUserDto);

        return Mono.empty();
    }

    @Override
    public Mono<Void> onDelete(UserEntity user) {
        //                    eventService.broadcastInternally(EventType.DELETED_USER, UserDto.builder(userEntity).build());
//        eventService.sendEventToUsers(UserRole.ADMIN, EventType.DELETED_USER, UserDto.builder(userEntity).build());

        return Mono.empty();
    }
}
