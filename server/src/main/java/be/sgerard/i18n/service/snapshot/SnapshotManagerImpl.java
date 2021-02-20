package be.sgerard.i18n.service.snapshot;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.snapshot.SnapshotEntity;
import be.sgerard.i18n.model.snapshot.dto.SnapshotCreationDto;
import be.sgerard.i18n.model.snapshot.file.SnapshotMetadataDto;
import be.sgerard.i18n.repository.snapshot.SnapshotRepository;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.snapshot.listener.SnapshotListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static be.sgerard.i18n.support.FileUtils.*;

/**
 * Implementation of the {@link SnapshotManager snapshot manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class SnapshotManagerImpl implements SnapshotManager {

    /**
     * Buffer size for handling files.
     */
    public static final int BUFFER_SIZE = 4096;

    /**
     * JSON file containing metadata.
     */
    public static final String METADATA_FILE = "metadata.json";

    private static final Logger logger = LoggerFactory.getLogger(SnapshotManagerImpl.class);

    private final SnapshotRepository snapshotRepository;
    private final AuthenticationUserManager authenticationUserManager;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;
    private final List<SnapshotHandler> handlers;
    private final SnapshotListener listener;

    public SnapshotManagerImpl(SnapshotRepository snapshotRepository,
                               AuthenticationUserManager authenticationUserManager,
                               AppProperties appProperties,
                               ObjectMapper objectMapper,
                               List<SnapshotHandler> handlers,
                               SnapshotListener listener) {
        this.snapshotRepository = snapshotRepository;
        this.authenticationUserManager = authenticationUserManager;
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
        this.handlers = handlers;
        this.listener = listener;
    }

    @Override
    public Flux<SnapshotEntity> findAll() {
        return snapshotRepository.findAll();
    }

    @Override
    public Mono<SnapshotEntity> findById(String id) {
        return snapshotRepository.findById(id);
    }

    @Override
    public Mono<SnapshotEntity> create(SnapshotCreationDto creationDto) {
        final File tempDirectory = createTempDirectory("snapshot-import-");

        return authenticationUserManager
                .getCurrentUserOrDie()
                .map(currentUser -> {
                    final SnapshotEntity snapshot = new SnapshotEntity(Instant.now(), currentUser.getDisplayName(), creationDto.getComment().orElse(null));

                    snapshot.setOriginalFileName(String.format("i18n-tool-%s.zip", snapshot.getCreatedOn().toString()));
                    snapshot.setZipFile(getSnapshotZipFile(snapshot));
                    snapshot.setEncryptionPassword(creationDto.getEncryptionPassword().orElse(null));

                    return snapshot;
                })
                .flatMap(snap -> writeMetadata(snap, tempDirectory))
                .flatMap(snap -> exportAll(snap, tempDirectory))
                .flatMap(snap -> zipSnapshot(snap, creationDto, tempDirectory))
                .doFinally(signalType -> deleteDirectory(tempDirectory))
                .flatMap(snapshotRepository::save)
                .flatMap(rep -> listener.afterPersist(rep).thenReturn(rep));
    }

    @Override
    public Mono<Pair<String, Flux<DataBuffer>>> exportZip(String id) {
        return findByIdOrDie(id)
                .map(snapshot ->
                        Pair.of(
                                snapshot.getOriginalFileName(),
                                DataBufferUtils.read(
                                        new FileSystemResource(snapshot.getZipFileAsJavaFile()),
                                        new DefaultDataBufferFactory(),
                                        BUFFER_SIZE
                                )
                        )
                );
    }

    @Override
    public Mono<SnapshotEntity> importZip(Function<File, Mono<Void>> fileTransfer, String fileName, String encryptionPassword) {
        final File tempDirectory = createTempDirectory("snapshot-import-");
        final File tempZipFile = new File(tempDirectory, String.format("%s.zip", UUID.randomUUID().toString()));

        return fileTransfer
                .apply(tempZipFile)
                .then(readMetadata(tempZipFile, tempDirectory, encryptionPassword))
                .map(metadata -> {
                    final SnapshotEntity snapshot = new SnapshotEntity(metadata.getCreatedOn(), metadata.getCreatedBy(), metadata.getComment().orElse(null));

                    snapshot.setOriginalFileName(fileName);
                    snapshot.setZipFile(getSnapshotZipFile(snapshot));
                    snapshot.setEncryptionPassword(encryptionPassword);

                    return snapshot;
                })
                .flatMap(snapshot -> moveSnapshotZipFile(tempZipFile, snapshot))
                .doFinally(signalType -> deleteDirectory(tempDirectory))
                .flatMap(snapshotRepository::save)
                .flatMap(rep -> listener.afterPersist(rep).thenReturn(rep));
    }

    @Override
    public Mono<SnapshotEntity> restore(String id) {
        final File tempDirectory = createTempDirectory("snapshot-import-");

        return findByIdOrDie(id)
                .flatMap(snapshot -> unzipSnapshot(snapshot, tempDirectory))
                .flatMap(this::clearAll)
                .flatMap(snapshot -> restoreAll(snapshot, tempDirectory))
                .doFinally(signalType -> deleteDirectory(tempDirectory));
    }

    @Override
    public Mono<Void> delete(String id) {
        return findByIdOrDie(id)
                .doOnNext(snapshot -> {
                    try {
                        final File file = snapshot.getZipFileAsJavaFile();

                        if (file.exists()) {
                            if (!file.delete()) {
                                logger.warn("Cannot remove snapshot file [" + file + "].");
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Error while deleting snapshot ZIP file.", e);
                    }
                })
                .flatMap(snapshot -> snapshotRepository.delete(snapshot).thenReturn(snapshot))
                .flatMap(listener::afterDelete);
    }

    /**
     * Returns the base directory containing snapshots.
     */
    private File getSnapshotDir() {
        return new File(new File(appProperties.getBaseDirectory()), "snapshot");
    }

    /**
     * Clear all existing objects from the tool.
     */
    private Mono<SnapshotEntity> clearAll(SnapshotEntity snapshot) {
        return Flux.fromIterable(handlers)
                .sort(Comparator.comparingInt(SnapshotHandler::getImportPriority).reversed())
                .flatMap(SnapshotHandler::clearAll, 1, 1)
                .then(Mono.just(snapshot));
    }

    /**
     * Exports all existing objects to the specified directory.
     */
    private Mono<SnapshotEntity> exportAll(SnapshotEntity snapshot, File tempDirectory) {
        return Flux.fromIterable(handlers)
                .sort(Comparator.comparingInt(SnapshotHandler::getImportPriority))
                .flatMap(handler -> handler.exportAll(tempDirectory), 1, 1)
                .then(Mono.just(snapshot));
    }

    /**
     * Restores all the objects from the specified snapshot.
     */
    private Mono<SnapshotEntity> restoreAll(SnapshotEntity snapshot, File importLocation) {
        return Flux.fromIterable(handlers)
                .sort(Comparator.comparingInt(SnapshotHandler::getImportPriority))
                .flatMap(handler ->
                                handler
                                        .validate(importLocation)
                                        .map(validationResult -> {
                                            ValidationException.throwIfFailed(validationResult);

                                            return handler;
                                        }),
                        1,
                        1
                )
                .flatMap(handler -> handler.restoreAll(importLocation))
                .then(Mono.just(snapshot));
    }

    /**
     * Returns the file name where storing the specified snapshot.
     */
    private String getSnapshotZipFile(SnapshotEntity snapshot) {
        return new File(getSnapshotDir(), String.format("%s.zip", snapshot.getId())).toString();
    }

    /**
     * Writes metadata as a JSON file in the specified directory.
     */
    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    private Mono<SnapshotEntity> writeMetadata(SnapshotEntity snapshot, File directory) {
        final File metadataFile = new File(directory, METADATA_FILE);

        try {
            // NICE find a better way
            objectMapper.writeValue(metadataFile, snapshot.toMetadata());

            return Mono.just(snapshot);
        } catch (IOException e) {
            throw new IllegalStateException("Error while writing metadata to file [" + metadataFile + "].", e);
        }
    }

    /**
     * Zips the content of the snapshot available in the specified directory.
     */
    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    private Mono<SnapshotEntity> zipSnapshot(SnapshotEntity snapshot, SnapshotCreationDto creationDto, File directory) {
        try {
            snapshot.getZipFileAsJavaFile().getParentFile().mkdirs();

            // NICE find a better way
            zipDirectory(directory, snapshot.getZipFileAsJavaFile(), creationDto.getEncryptionPassword().orElse(null));

            return Mono.just(snapshot);
        } catch (ZipException e) {
            throw new IllegalStateException("Error while zipping snapshot files at location [" + directory + "].", e);
        }
    }

    /**
     * Reads the {@link SnapshotMetadataDto metadata} contained in the specified zip file. The ZIP is eventually
     * encrypted with the specified password (can be <tt>null</tt>). The zip can be unzipped at the specified directory.
     */
    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    private Mono<SnapshotMetadataDto> readMetadata(File zipFile, File tempDirectory, String encryptionPassword) {
        try {
            unzipDirectory(tempDirectory, zipFile, encryptionPassword);

            return Mono.just(
                    objectMapper.readValue(new File(tempDirectory, METADATA_FILE), SnapshotMetadataDto.class)
            );
        } catch (Exception e) {
            throw SnapshotException.onReadingMetadata(e);
        }
    }

    /**
     * Moves the ZIP file to tits final destination specified by the snapshot.
     */
    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    private Mono<SnapshotEntity> moveSnapshotZipFile(File zipFile, SnapshotEntity snapshot) {
        try {
            snapshot.getZipFileAsJavaFile().getParentFile().mkdirs();

            // NICE find a better way
            Files.move(zipFile.toPath(), snapshot.getZipFileAsJavaFile().toPath());

            return Mono.just(snapshot);
        } catch (IOException e) {
            throw new IllegalStateException("Error while moving the file [" + zipFile + "] to [" + snapshot.getZipFile() + "].", e);
        }
    }

    /**
     * Unzips the snapshot file to the specified directory.
     */
    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    private Mono<SnapshotEntity> unzipSnapshot(SnapshotEntity snapshot, File directory) {
        try {
            // NICE find a better way
            unzipDirectory(directory, snapshot.getZipFileAsJavaFile(), snapshot.getEncryptionPassword().orElse(null));

            return Mono.just(snapshot);
        } catch (Exception e) {
            throw SnapshotException.onUnzippingExistingSnapshot(e);
        }
    }
}
