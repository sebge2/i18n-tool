package be.sgerard.i18n.service.security.session.listener;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link UserLiveSessionListener session listener}.
 * @author Sebastien Gerard
 */
@Primary
@Component
public class CompositeUserLiveSessionListener implements UserLiveSessionListener {

    private final List<UserLiveSessionListener> listeners;

    public CompositeUserLiveSessionListener(List<UserLiveSessionListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<Void> onNewSession(UserLiveSessionEntity userLiveSession) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onNewSession(userLiveSession))
                .then();
    }

    @Override
    public Mono<Void> onStopSession(UserLiveSessionEntity userLiveSession) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onStopSession(userLiveSession))
                .then();
    }
}
