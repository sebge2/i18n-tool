package be.sgerard.poc.githuboauth.service.security.session;

import be.sgerard.poc.githuboauth.model.security.session.UserSessionEntity;

import java.util.Collection;

/**
 * @author Sebastien Gerard
 */
public interface UserSessionManager {

    Collection<UserSessionEntity> getCurrentSessions();
}
