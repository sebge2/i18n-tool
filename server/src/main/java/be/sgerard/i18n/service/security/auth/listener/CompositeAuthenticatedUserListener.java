package be.sgerard.i18n.service.security.auth.listener;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link AuthenticatedUserListener authenticated user listener}.
 *
 * @author Sebastien Gerard
 */
@Primary
@Component
public class CompositeAuthenticatedUserListener implements AuthenticatedUserListener {

    private final List<AuthenticatedUserListener> listeners;

    @Lazy
    public CompositeAuthenticatedUserListener(List<AuthenticatedUserListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<Void> onUpdate(AuthenticatedUser authenticatedUser) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onUpdate(authenticatedUser))
                .then();
    }

    @Override
    public Mono<Void> onDelete(AuthenticatedUser authenticatedUser) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onDelete(authenticatedUser))
                .then();
    }
}
