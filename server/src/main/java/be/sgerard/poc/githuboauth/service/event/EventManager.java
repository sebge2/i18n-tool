package be.sgerard.poc.githuboauth.service.event;

import be.sgerard.poc.githuboauth.model.auth.UserEntity;

/**
 * @author Sebastien Gerard
 */
public interface EventManager {

    void broadcastEvent(Object event);

    void sendEventToUser(UserEntity user, Object event);
}
