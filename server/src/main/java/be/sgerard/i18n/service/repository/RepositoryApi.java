package be.sgerard.i18n.service.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

/**
 * API for accessing a repository.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryApi {

    /**
     * Returns all the available branches.
     */
    Flux<String> listBranches() throws RepositoryException;

    /**
     * Lists recursively all files (normal files and directories) at the specified location on the specified branch.
     */
    Flux<File> listAllFiles(String branch, File file) throws IOException;

    /**
     * Lists recursively all normal files (not directories) at the specified location on the specified branch.
     */
    Flux<File> listNormalFiles(String branch, File file) throws IOException;

    /**
     * Lists recursively all directories at the specified location on the specified branch.
     */
    Flux<File> listDirectories(String branch, File file) throws IOException;

    /**
     * {@link FunctionalInterface Functional interface} for consuming the API.
     */
    @FunctionalInterface
    interface ApiConsumer {

        /**
         * Consumes the specified {@link RepositoryApi API}.
         */
        Mono<Void> consume(RepositoryApi api) throws RepositoryException;

    }

    /**
     * {@link FunctionalInterface Functional interface} for applying a function using the API.
     */
    @FunctionalInterface
    interface ApiFunction<T> {

        /**
         * Consumes the specified {@link RepositoryApi API} and returns a value.
         */
        T apply(RepositoryApi api) throws RepositoryException;

    }
}
