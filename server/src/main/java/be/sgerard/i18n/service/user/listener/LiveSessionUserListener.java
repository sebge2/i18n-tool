package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.session.UserLiveSessionManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link UserListener User listener} deleting all
 * {@link be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity live sessions}
 * when the user is deleted.
 *
 * @author Sebastien Gerard
 */
@Component
public class LiveSessionUserListener implements UserListener {

    private final UserLiveSessionManager liveSessionManager;

    public LiveSessionUserListener(UserLiveSessionManager liveSessionManager) {
        this.liveSessionManager = liveSessionManager;
    }

    @Override
    public Mono<Void> onDelete(UserEntity user) {
        return liveSessionManager.deleteAll(user);
    }

}
