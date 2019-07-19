package be.sgerard.poc.githuboauth.model.security.session;

import be.sgerard.poc.githuboauth.model.event.ApplicationEvent;

/**
 * @author Sebastien Gerard
 */
public class DisconnectedUserSessionEvent extends ApplicationEvent {

    public DisconnectedUserSessionEvent(UserSessionDto userSession) {
        super(userSession, "disconnected-user-session");
    }
}
