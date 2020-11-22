package be.sgerard.i18n.service.snapshot;

import be.sgerard.i18n.model.snapshot.SnapshotEntity;
import be.sgerard.i18n.model.snapshot.dto.SnapshotCreationDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.util.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.function.Function;

/**
 * Manager of {@link SnapshotEntity snapshots} freezing the current tool state.
 *
 * @author Sebastien Gerard
 */
public interface SnapshotManager {

    /**
     * Finds all available {@link SnapshotEntity snapshots}.
     */
    Flux<SnapshotEntity> findAll();

    /**
     * Returns the {@link SnapshotEntity snapshot} having the specified id.
     */
    Mono<SnapshotEntity> findById(String id);

    /**
     * Returns the {@link SnapshotEntity snapshot} having the specified id.
     */
    default Mono<SnapshotEntity> findByIdOrDie(String id) throws ResourceNotFoundException {
        return findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.snapshotNotFoundException(id)));
    }

    /**
     * Creates a new {@link SnapshotEntity snapshot} based on the current tool state.
     */
    Mono<SnapshotEntity> create(SnapshotCreationDto creationDto);

    /**
     * Exports the content of the specified snapshot. The first element of the pair, is the original file name, the second
     * is the file content.
     */
    Mono<Pair<String, Flux<DataBuffer>>> exportZip(String id);

    /**
     * Imports a snapshot ZIP file using the function that moves the content of the file to
     * the specified location. The optional password used to encrypt the ZIP file (can be <tt>null</tt>).
     */
    Mono<SnapshotEntity> importZip(Function<File, Mono<Void>> fileTransfer, String fileName, String encryptionPassword);

    /**
     * Restores the specified snapshot, the tool state will be restored to that state.
     */
    Mono<SnapshotEntity> restore(String id);

    /**
     * Deletes the specified snapshot.
     */
    Mono<Void> delete(String id);
}
