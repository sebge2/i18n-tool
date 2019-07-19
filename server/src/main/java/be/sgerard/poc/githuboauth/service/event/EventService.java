package be.sgerard.poc.githuboauth.service.event;

import be.sgerard.poc.githuboauth.model.event.ApplicationEvent;
import be.sgerard.poc.githuboauth.model.security.user.UserEntity;

/**
 * @author Sebastien Gerard
 */
public interface EventService {

    String ALL_TOPIC_EVENT = "/topic/";

    String USER_TOPIC_EVENT = "/queue/";

    void broadcastInternally(ApplicationEvent event);

    void broadcastEvent(ApplicationEvent event);

    void sendEventToUser(UserEntity user, ApplicationEvent event);
}
