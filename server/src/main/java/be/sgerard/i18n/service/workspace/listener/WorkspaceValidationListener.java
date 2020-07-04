package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
    public Mono<ValidationResult> beforeFinishReview(WorkspaceEntity workspace) {
        if (workspace.getStatus() != WorkspaceStatus.IN_REVIEW) {
//            return ValidationResult.builder()
//                    .messages(new ValidationMessage("validation.workspace.cannot-finish-review", workspace.getId()))
//                    .build();
        }

        return Mono.just(ValidationResult.EMPTY);
    }

    @Override
    public Mono<ValidationResult> beforeInitialize(WorkspaceEntity workspace) {
//        if (workspace.getStatus() == WorkspaceStatus.INITIALIZED) {
//            return workspace;
//        } else if (workspace.getStatus() != WorkspaceStatus.NOT_INITIALIZED) {
//            throw new IllegalStateException("The workspace status must be available, but was " + workspace.getStatus() + ".");
//        }

        return Mono.just(ValidationResult.EMPTY);
    }

    @Override
    public Mono<ValidationResult> beforePublish(WorkspaceEntity workspace) {
//                        if (workspace.getStatus() == WorkspaceStatus.IN_REVIEW) {
//                    return workspace;
//                } else if (workspace.getStatus() != WorkspaceStatus.INITIALIZED) {
//                    throw new IllegalStateException("The workspace status must be available, but was " + workspace.getStatus() + ".");
//                }

        return Mono.just(ValidationResult.EMPTY);
    }
}
