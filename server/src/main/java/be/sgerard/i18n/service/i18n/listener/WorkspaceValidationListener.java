package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.WorkspaceStatus;
import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;

/**
 * {@link WorkspaceListener Workspace listener} performing validation.
 *
 * @author Sebastien Gerard
 */
@Component
public class WorkspaceValidationListener implements WorkspaceListener {

    public WorkspaceValidationListener() {
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }

    @Override
    public ValidationResult beforeFinishReview(WorkspaceEntity workspace) {
        if (workspace.getStatus() != WorkspaceStatus.IN_REVIEW) {
            return ValidationResult.builder()
                    .messages(new ValidationMessage("validation.workspace.cannot-finish-review", workspace.getId()))
                    .build();
        }

        return ValidationResult.EMPTY;
    }
}
