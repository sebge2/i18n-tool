package be.sgerard.i18n.service.event;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import reactor.core.publisher.Mono;

/**
 * Service sending events to web-applications.
 *
 * @author Sebastien Gerard
 */
public interface EventService {

    /**
     * Broadcasts the specified event to all connected users.
     */
    Mono<Void> broadcastEvent(EventType eventType, Object payload);

    /**
     * Broadcasts the specified event to all connected users having the specified {@link UserRole role}.
     */
    Mono<Void> sendEventToUsers(UserRole userRole, EventType eventType, Object payload);

    /**
     * Broadcasts the specified event to the specified user.
     */
    Mono<Void> sendEventToUser(UserDto user, EventType eventType, Object payload);

    /**
     * Broadcasts the specified event to to the specified user session (a user may have multiple sessions).
     */
    Mono<Void> sendEventToSession(String sessionId, EventType eventType, Object payload);

}
