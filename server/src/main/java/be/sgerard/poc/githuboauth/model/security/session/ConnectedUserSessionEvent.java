package be.sgerard.poc.githuboauth.model.security.session;

import be.sgerard.poc.githuboauth.model.event.ApplicationEvent;

/**
 * @author Sebastien Gerard
 */
public class ConnectedUserSessionEvent extends ApplicationEvent {

    public ConnectedUserSessionEvent(UserSessionDto userSession) {
        super(userSession, "connected-user-session");
    }
}
