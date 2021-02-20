package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.user.dto.UserDto;
import be.sgerard.i18n.model.user.dto.UserPreferencesDto;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link UserPreferencesListener Listener} emitting events when preferences change.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserPreferencesEventListener implements UserPreferencesListener {

    private final EventService eventService;

    public UserPreferencesEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Mono<Void> afterUpdate(UserEntity user) {
        return eventService.sendEventToUser(
                UserDto.builder(user).build(),
                EventType.UPDATED_USER_PREFERENCES,
                UserPreferencesDto.builder(user.getPreferences()).build()
        );
    }
}
