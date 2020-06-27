package be.sgerard.i18n.model.repository;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Sebastien Gerard
 */
@Schema(description = "All possible repository statuses.")
public enum RepositoryStatus {

    NOT_INITIALIZED,

    INITIALIZING,

    INITIALIZED,

    INITIALIZATION_ERROR
}
