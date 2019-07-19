package be.sgerard.poc.githuboauth.service.event;

import be.sgerard.poc.githuboauth.model.event.ApplicationEvent;
import be.sgerard.poc.githuboauth.model.security.user.UserEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * @author Sebastien Gerard
 */
@Service
public class StompEventService implements EventService {

    private final SimpMessageSendingOperations template;
    private final ApplicationEventPublisher eventPublisher;

    public StompEventService(SimpMessageSendingOperations template, ApplicationEventPublisher eventPublisher) {
        this.template = template;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void broadcastInternally(ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void broadcastEvent(ApplicationEvent event) {
        broadcastInternally(event);

        template.convertAndSend(ALL_TOPIC_EVENT + "/" + event.getType(), event.getSource());
    }

    @Override
    public void sendEventToUser(UserEntity user, ApplicationEvent event) {
        template.convertAndSendToUser(user.getId(), USER_TOPIC_EVENT + "/" + event.getType(), event.getSource());
    }

}
