package be.sgerard.i18n.service.workspace.listener;

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

    @Override
    public ValidationResult beforeInitialize(WorkspaceEntity workspace) {
//        if (workspaceEntity.getStatus() == WorkspaceStatus.INITIALIZED) {
//            return workspaceEntity;
//        } else if (workspaceEntity.getStatus() != WorkspaceStatus.NOT_INITIALIZED) {
//            throw new IllegalStateException("The workspace status must be available, but was " + workspaceEntity.getStatus() + ".");
//        }

        return ValidationResult.EMPTY;
    }

    @Override
    public ValidationResult beforePublish(WorkspaceEntity workspace) {
        //                if (workspaceEntity.getStatus() == WorkspaceStatus.IN_REVIEW) {
//                    return workspaceEntity;
//                } else if (workspaceEntity.getStatus() != WorkspaceStatus.INITIALIZED) {
//                    throw new IllegalStateException("The workspace status must be available, but was " + workspaceEntity.getStatus() + ".");
//                }

        return ValidationResult.EMPTY;
    }
}
