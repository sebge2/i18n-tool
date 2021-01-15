package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link UserPreferencesListener user preferences listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeUserPreferencesListener implements UserPreferencesListener {

    private final List<UserPreferencesListener> listeners;

    @Lazy
    public CompositeUserPreferencesListener(List<UserPreferencesListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<Void> afterUpdate(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(user))
                .then();
    }
}
