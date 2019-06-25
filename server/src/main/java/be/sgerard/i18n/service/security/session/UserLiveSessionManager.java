package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.security.session.UserLiveSessionEntity;

import java.util.Collection;

/**
 * @author Sebastien Gerard
 */
public interface UserLiveSessionManager {

    Collection<UserLiveSessionEntity> getCurrentLiveSessions();

}
