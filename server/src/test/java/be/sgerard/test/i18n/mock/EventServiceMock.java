package be.sgerard.test.i18n.mock;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author Sebastien Gerard
 */
@Primary
@Service
public class EventServiceMock implements EventService {

    public EventServiceMock() {
    }

    // TODO

    @Override
    public Mono<Void> broadcastEvent(EventType eventType, Object payload) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> sendEventToUsers(UserRole userRole, EventType eventType, Object payload) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> sendEventToUser(UserDto user, EventType eventType, Object payload) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> sendEventToSession(String sessionId, EventType eventType, Object payload) {
        return Mono.empty();
    }
}
