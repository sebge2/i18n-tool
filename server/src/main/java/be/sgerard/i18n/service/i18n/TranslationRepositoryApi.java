package be.sgerard.i18n.service.i18n;

import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;

/**
 * API exposed to the {@link TranslationManager translation manager} which is agnostic from the underlying
 * repository technology.
 *
 * @author Sebastien Gerard
 */
public interface TranslationRepositoryApi {

    /**
     * Lists recursively all files (normal files and directories) at the specified location.
     */
    Flux<File> listAllFiles(File file) throws IOException;

    /**
     * Lists recursively all normal files (not directories) at the specified location.
     */
    Flux<File> listNormalFiles(File file) throws IOException;

    /**
     * Lists recursively all directories at the specified location.
     */
    Flux<File> listDirectories(File file) throws IOException;

}
