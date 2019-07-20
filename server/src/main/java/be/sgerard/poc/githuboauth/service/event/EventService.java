package be.sgerard.poc.githuboauth.service.event;

import be.sgerard.poc.githuboauth.model.security.user.UserEntity;

/**
 * @author Sebastien Gerard
 */
public interface EventService {

    void broadcastEvent(String eventType, Object payload);

    void sendEventToUser(UserEntity user, String eventType, Object payload);
}
