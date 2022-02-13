package be.sgerard.i18n.service.security.session.listener;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.session.UserLiveSessionDtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link UserLiveSessionListener Listener} emitting events about new/stopped user live sessions.
 *
 * @author Sebastien Gerard
 */
@Component
@AllArgsConstructor
public class UserLiveSessionEventListener implements UserLiveSessionListener {

    private final EventService eventService;
    private final UserLiveSessionDtoMapper dtoMapper;

    @Override
    public Mono<Void> afterStarting(UserLiveSessionEntity session) {
        return eventService.broadcastEvent(EventType.CONNECTED_USER_SESSION, dtoMapper.toDto(session));
    }

    @Override
    public Mono<Void> afterStopping(UserLiveSessionEntity session) {
        return eventService.broadcastEvent(EventType.DISCONNECTED_USER_SESSION, dtoMapper.toDto(session));
    }

    @Override
    public Mono<Void> afterDeletion(UserLiveSessionEntity session) {
        return eventService.broadcastEvent(EventType.DISCONNECTED_USER_SESSION, dtoMapper.toDto(session));
    }
}
