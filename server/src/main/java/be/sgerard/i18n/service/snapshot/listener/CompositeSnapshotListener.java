package be.sgerard.i18n.service.snapshot.listener;

import be.sgerard.i18n.model.snapshot.SnapshotEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link SnapshotListener snapshot listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeSnapshotListener implements SnapshotListener {

    private final List<SnapshotListener> listeners;

    public CompositeSnapshotListener(List<SnapshotListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<Void> afterPersist(SnapshotEntity snapshot) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterPersist(snapshot))
                .then();
    }

    @Override
    public Mono<Void> afterDelete(SnapshotEntity snapshot) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterDelete(snapshot))
                .then();
    }
}
