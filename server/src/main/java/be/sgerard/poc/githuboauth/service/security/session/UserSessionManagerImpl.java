package be.sgerard.poc.githuboauth.service.security.session;

import be.sgerard.poc.githuboauth.model.security.user.UserEntity;
import be.sgerard.poc.githuboauth.service.security.auth.AuthenticationManager;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author Sebastien Gerard
 */
@Service
public class UserSessionManagerImpl implements UserSessionManager {

    private final AuthenticationManager authenticationManager;
    private final UserSessionManagerRepository repository;

    public UserSessionManagerImpl(AuthenticationManager authenticationManager, UserSessionManagerRepository repository) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
    }

    @EventListener
    public void onSessionConnectedEvent(SessionConnectedEvent event) {
        final String sessionId = getSessionId(event);

        final UserEntity user = authenticationManager.getUserFromPrincipal(event.getUser());

        System.out.println("connect");
        System.out.println(user);
        System.out.println(sessionId);
    }

    @EventListener
    public void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        final String sessionId = getSessionId(event);

        final UserEntity user = authenticationManager.getUserFromPrincipal(event.getUser());

        System.out.println("disconnect");
        System.out.println(user);
        System.out.println(sessionId);
    }

    private String getSessionId(AbstractSubProtocolEvent event) {
        final MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);

        if (accessor == null) {
            throw new IllegalStateException("Cannot extract session from null accessor.");
        }

        return SimpMessageHeaderAccessor.getSessionId(accessor.getMessageHeaders());
    }
}
