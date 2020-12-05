package be.sgerard.i18n.repository.snapshot;

import be.sgerard.i18n.model.snapshot.SnapshotEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * {@link ReactiveMongoRepository Repository} of {@link SnapshotEntity snapshots}.
 *
 * @author Sebastien Gerard
 */
public interface SnapshotRepository extends ReactiveMongoRepository<SnapshotEntity, String> {

}
