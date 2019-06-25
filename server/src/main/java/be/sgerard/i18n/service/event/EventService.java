package be.sgerard.i18n.service.event;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.service.security.UserRole;

/**
 * @author Sebastien Gerard
 */
public interface EventService {

    void broadcastInternally(EventType eventType, Object payload);

    void broadcastEvent(EventType eventType, Object payload);

    void sendEventToUsers(UserRole userRole, EventType eventType, Object payload);

    void sendEventToUser(UserDto user, EventType eventType, Object payload);

    void sendEventToSession(String simpSessionId, EventType eventType, Object payload);

    void addListener(InternalEventListener<?> listener);
}
