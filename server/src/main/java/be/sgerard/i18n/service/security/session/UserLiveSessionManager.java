package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.security.session.UserLiveSessionEntity;
import reactor.core.publisher.Flux;

/**
 * Manager of {@link UserLiveSessionEntity user live sessions}.
 *
 * @author Sebastien Gerard
 */
public interface UserLiveSessionManager {

    /**
     * Returns all the current {@link UserLiveSessionEntity user live session}.
     */
    Flux<UserLiveSessionEntity> getCurrentLiveSessions();

}
