package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.service.repository.RepositoryException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStream;

/**
 * API exposed to the {@link TranslationManager translation manager} which is agnostic from the underlying
 * repository technology. This API is used to discover and read files from the repository.
 *
 * @author Sebastien Gerard
 */
public interface TranslationRepositoryReadApi {

    /**
     * Lists recursively all files (normal files and directories) at the specified location.
     */
    Flux<File> listAllFiles(File file) throws RepositoryException;

    /**
     * Lists recursively all normal files (not directories) at the specified location.
     */
    Flux<File> listNormalFiles(File file) throws RepositoryException;

    /**
     * Lists recursively all directories at the specified location.
     */
    Flux<File> listDirectories(File file) throws RepositoryException;

    /**
     * Opens the specified file.
     */
    Mono<InputStream> openInputStream(File file) throws RepositoryException;

    /**
     * Creates a temporary of the specified file and returns it. Once the API will be closed, the temporary file will be dropped.
     */
    Mono<File> openAsTemp(File file) throws RepositoryException;

}
