package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.persistence.UserPreferencesEntity;
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
    public Mono<Void> onUpdate(UserPreferencesEntity preferences) {
        // TODO
        //        eventService.sendEventToUser(UserDto.builder(existingPreferences.getUser()).build(), EventType.UPDATED_USER_PREFERENCES, preferences);

        return Mono.empty();
    }
}
