package be.sgerard.i18n.model.repository;

import io.swagger.annotations.ApiModel;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "All possible repository statuses.")
public enum RepositoryStatus {

    NOT_INITIALIZED,

    INITIALIZING,

    INITIALIZED
}
