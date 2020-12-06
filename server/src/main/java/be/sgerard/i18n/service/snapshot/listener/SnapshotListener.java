package be.sgerard.i18n.service.snapshot.listener;

import be.sgerard.i18n.model.snapshot.SnapshotEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of the lifecycle of {@link SnapshotEntity snapshots}.
 *
 * @author Sebastien Gerard
 */
public interface SnapshotListener {

    /**
     * Performs an action after the creation of the specified snapshot.
     */
    default Mono<Void> afterCreate(SnapshotEntity snapshot) {
        return Mono.empty();
    }

    /**
     * Performs an action before the deletion of the specified snapshot.
     */
    default Mono<Void> beforeDelete(SnapshotEntity snapshot) {
        return Mono.empty();
    }

}
