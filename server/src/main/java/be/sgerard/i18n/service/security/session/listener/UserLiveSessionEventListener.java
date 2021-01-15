package be.sgerard.i18n.service.security.session.listener;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.session.dto.UserLiveSessionDto;
import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link UserLiveSessionListener Listener} emitting events about new/stopped user live sessions.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserLiveSessionEventListener implements UserLiveSessionListener {

    private final EventService eventService;

    public UserLiveSessionEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Mono<Void> afterStarting(UserLiveSessionEntity session) {
        return eventService.broadcastEvent(EventType.CONNECTED_USER_SESSION, UserLiveSessionDto.toDto(session));
    }

    @Override
    public Mono<Void> afterStopping(UserLiveSessionEntity session) {
        return eventService.broadcastEvent(EventType.DISCONNECTED_USER_SESSION, UserLiveSessionDto.toDto(session));
    }

    @Override
    public Mono<Void> afterDeletion(UserLiveSessionEntity session) {
        return eventService.broadcastEvent(EventType.DISCONNECTED_USER_SESSION, UserLiveSessionDto.toDto(session));
    }
}
