package be.sgerard.i18n.service.event;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.session.UserLiveSessionManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link EventService Event service} based on the STOMP protocol.
 *
 * @author Sebastien Gerard
 */
@Service
public class StompEventService implements EventService {

    private final SimpMessageSendingOperations template;
    private final UserLiveSessionManager userSessionManager;
    private final List<InternalEventListener<Object>> eventListeners = new ArrayList<>();

    public StompEventService(SimpMessageSendingOperations template,
                             @Lazy UserLiveSessionManager userSessionManager) {
        this.template = template;
        this.userSessionManager = userSessionManager;
    }

//    @Override
//    public void broadcastInternally(EventType eventType, Object payload) {
//        eventListeners.stream()
//                .filter(listener -> listener.support(eventType))
//                .forEach(listener -> listener.onEvent(payload));
//    }

    @Override
    public Mono<Void> broadcastEvent(EventType eventType, Object payload) {
        sendEventToUsers(UserRole.MEMBER_OF_ORGANIZATION, eventType, payload);
        return Mono.empty();
    }

    // TODO
    @Override
    public Mono<Void> sendEventToUsers(UserRole userRole, EventType eventType, Object payload) {
//        userSessionManager.getCurrentLiveSessions().stream()
//                .filter(session -> session.getSessionRoles().contains(userRole))
//                .distinct()
//                .forEach(session -> sendEventToSession(session.getSimpSessionId(), eventType, payload));
        return Mono.empty();
    }

    @Override
    public Mono<Void> sendEventToUser(UserDto user, EventType eventType, Object payload) {
        template.convertAndSendToUser(user.getId(), eventType.toUserQueue(), payload);
        return Mono.empty();
    }

    @Override
    public Mono<Void> sendEventToSession(String sessionId, EventType eventType, Object payload) {
        final SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        template.convertAndSendToUser(sessionId, eventType.toUserQueue(), payload, headerAccessor.getMessageHeaders());

        return Mono.empty();
    }

//    @Override
//    @SuppressWarnings({"unchecked", "RedundantCast"})
//    public void addListener(InternalEventListener<?> listener) {
//        this.eventListeners.add((InternalEventListener<Object>) (Object) listener);
//    }
}
