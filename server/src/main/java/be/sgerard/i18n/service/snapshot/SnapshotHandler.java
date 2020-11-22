package be.sgerard.i18n.service.snapshot;

import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

import java.io.File;

/**
 * Handler of a particular kind of object that will be frozen by a snapshot.
 *
 * @author Sebastien Gerard
 */
public interface SnapshotHandler {

    /**
     * Returns the priority in which the handler will be called when importing (ordered by ascending priority).
     * For clearing, they will be called in the opposite order.
     */
    int getImportPriority();

    /**
     * Clears all the entities handled by this component.
     */
    Mono<Void> clearAll();

    /**
     * Validates entities from the snapshot unzipped at the specified location.
     */
    Mono<ValidationResult> validate(File importLocation);

    /**
     * Restores all the entities from the snapshot unzipped at the specified location.
     */
    Mono<Void> restoreAll(File importLocation);

    /**
     * Exports all the entities to the directory at the specified location. After that, this directory will be zipped to get the snapshot file.
     */
    Mono<Void> exportAll(File exportLocation);

}
