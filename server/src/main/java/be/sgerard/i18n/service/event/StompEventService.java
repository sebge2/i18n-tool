package be.sgerard.i18n.service.event;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.session.UserLiveSessionManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * {@link EventService Event service} based on the STOMP protocol.
 *
 * @author Sebastien Gerard
 */
@Service
public class StompEventService implements EventService {

    private final SimpMessageSendingOperations template;
    private final UserLiveSessionManager userSessionManager;

    @Lazy
    public StompEventService(SimpMessageSendingOperations template, UserLiveSessionManager userSessionManager) {
        this.template = template;
        this.userSessionManager = userSessionManager;
    }

    @Override
    public Mono<Void> broadcastEvent(EventType eventType, Object payload) {
        return sendEventToUsers(UserRole.MEMBER_OF_ORGANIZATION, eventType, payload);
    }

    @Override
    public Mono<Void> sendEventToUsers(UserRole userRole, EventType eventType, Object payload) {
        return userSessionManager
                .getCurrentLiveSessions()
                .filter(session -> session.getSessionRoles().contains(userRole))
                .distinct()
                .flatMap(session -> sendEventToSession(session.getSimpSessionId(), eventType, payload))
                .then();
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
}
