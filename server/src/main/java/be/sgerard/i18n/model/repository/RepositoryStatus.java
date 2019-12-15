package be.sgerard.i18n.model.repository;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * All possible repository statuses.
 *
 * @author Sebastien Gerard
 */
@Schema(description = "All possible repository statuses.")
public enum RepositoryStatus {

    /**
     * The repository is registered, but it's data is not fetched yet.
     */
    NOT_INITIALIZED,

    /**
     * The repository is registered, data is fetched.
     */
    INITIALIZED,

    /**
     * An error occurred while initializing the repository.
     */
    INITIALIZATION_ERROR
}
