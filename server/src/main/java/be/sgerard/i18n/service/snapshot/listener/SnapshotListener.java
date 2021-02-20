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
     * Performs an action after the the specified snapshot has been persisted.
     */
    default Mono<Void> afterPersist(SnapshotEntity snapshot) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified snapshot.
     */
    default Mono<Void> afterDelete(SnapshotEntity snapshot) {
        return Mono.empty();
    }

}
