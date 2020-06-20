package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.session.UserLiveSessionDto;
import be.sgerard.i18n.model.security.session.UserLiveSessionEntity;
import be.sgerard.i18n.model.security.user.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.repository.security.UserLiveSessionManagerRepository;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.event.InternalEventListener;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Instant;
import java.util.Collection;

import static be.sgerard.i18n.model.event.EventType.UPDATED_CURRENT_AUTHENTICATED_USER;
import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.getAuthenticatedUserOrFail;

/**
 * @author Sebastien Gerard
 */
@Service
public class UserLiveSessionManagerImpl implements UserLiveSessionManager {

    private final UserManager userManager;
    private final UserLiveSessionManagerRepository repository;
    private final EventService eventService;

    public UserLiveSessionManagerImpl(UserManager userManager,
                                      UserLiveSessionManagerRepository repository,
                                      EventService eventService) {
        this.userManager = userManager;
        this.repository = repository;
        this.eventService = eventService;
//        this.eventService.addListener(new UpdatedAuthenticationUserListener());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserLiveSessionEntity> getCurrentLiveSessions() {
        return repository.findByLogoutTimeIsNull();
    }

    @EventListener
    @Transactional
    public void onSessionConnectedEvent(SessionConnectedEvent event) {
        final AuthenticatedUser authenticatedUser = getAuthenticatedUserOrFail(event.getUser());

        final UserEntity userEntity = userManager.findByIdOrDie(authenticatedUser.getUser().getId()).block(); // TODO

        final UserLiveSessionEntity sessionEntity = new UserLiveSessionEntity(
                userEntity,
                authenticatedUser.getId(),
                getSessionId(event),
                Instant.now(),
                authenticatedUser.getSessionRoles()
        );

        repository.save(sessionEntity);

        // TODO restrict visible info
        eventService.broadcastEvent(EventType.CONNECTED_USER_SESSION, UserLiveSessionDto.builder(sessionEntity).build());
    }

    @EventListener
    @Transactional
    public void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        final String sessionId = getSessionId(event);

        final UserLiveSessionEntity sessionEntity = repository.findBySimpSessionId(sessionId)
                .orElseThrow(() -> new IllegalStateException("There is no session with id [" + sessionId + "]."));


        if (sessionEntity.getLogoutTime() == null) {
            sessionEntity.setLogoutTime(Instant.now());

            // TODO restrict visible info
            eventService.broadcastEvent(EventType.DISCONNECTED_USER_SESSION, UserLiveSessionDto.builder(sessionEntity).build());
        }
    }

    private String getSessionId(AbstractSubProtocolEvent event) {
        final MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);

        if (accessor == null) {
            throw new IllegalStateException("Cannot extract session from null accessor.");
        }

        return SimpMessageHeaderAccessor.getSessionId(accessor.getMessageHeaders());
    }

    private final class UpdatedAuthenticationUserListener implements InternalEventListener<AuthenticatedUserDto> {

        @Override
        public boolean support(EventType eventType) {
            return eventType == EventType.UPDATED_AUTHENTICATED_USER;
        }

        @Override
        public void onEvent(AuthenticatedUserDto updatedAuthenticatedUser) {
            repository.findByAuthenticatedUserId(updatedAuthenticatedUser.getId())
                    .forEach(liveSession -> {
                        liveSession.setSessionRoles(updatedAuthenticatedUser.getSessionRoles());

                        eventService.sendEventToSession(
                                liveSession.getSimpSessionId(),
                                UPDATED_CURRENT_AUTHENTICATED_USER,
                                AuthenticatedUserDto.builder()
                                        .user(UserDto.builder(liveSession.getUser()).build())
                                        .sessionRoles(liveSession.getSessionRoles())
                                        .build()
                        );
                    });
        }
    }
}
