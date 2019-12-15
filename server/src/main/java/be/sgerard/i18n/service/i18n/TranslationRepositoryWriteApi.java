package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.service.repository.RepositoryException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.OutputStream;

/**
 * API exposed to the {@link TranslationManager translation manager} which is agnostic from the underlying
 * repository technology. This API is used to write to the repository.
 *
 * @author Sebastien Gerard
 */
public interface TranslationRepositoryWriteApi {

    /**
     * Creates a temporary of the specified file and returns it. Once the API will be closed, the temporary file will be dropped.
     */
    Mono<File> openAsTemp(File file) throws RepositoryException;

    /**
     * Creates an output stream to the specified file.
     */
    Mono<OutputStream> openOutputStream(File file) throws RepositoryException;
}
