package be.sgerard.i18n.service.security.session.listener;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.session.UserLiveSessionDtoMapper;
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
    private final UserLiveSessionDtoMapper mapper;

    public UserLiveSessionEventListener(EventService eventService, UserLiveSessionDtoMapper mapper) {
        this.eventService = eventService;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> onNewSession(UserLiveSessionEntity userLiveSession) {
        return mapper
                .toDto(userLiveSession)
                .flatMap(dto -> eventService.broadcastEvent(EventType.CONNECTED_USER_SESSION, dto));
    }

    @Override
    public Mono<Void> onStopSession(UserLiveSessionEntity userLiveSession) {
        return mapper
                .toDto(userLiveSession)
                .flatMap(dto -> eventService.broadcastEvent(EventType.DISCONNECTED_USER_SESSION, dto));
    }
}
