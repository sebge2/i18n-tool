package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.security.session.UserSessionEntity;

import java.util.Collection;

/**
 * @author Sebastien Gerard
 */
public interface UserSessionManager {

    Collection<UserSessionEntity> getCurrentSessions();
}
