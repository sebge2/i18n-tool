package be.sgerard.i18n.service.event;

import be.sgerard.i18n.model.security.user.UserEntity;

/**
 * @author Sebastien Gerard
 */
public interface EventService {

    void broadcastEvent(String eventType, Object payload);

    void sendEventToUser(UserEntity user, String eventType, Object payload);
}
