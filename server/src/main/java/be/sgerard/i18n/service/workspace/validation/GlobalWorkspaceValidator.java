package be.sgerard.i18n.service.workspace.validation;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Sebastien Gerard
 */
@Component
public class GlobalWorkspaceValidator implements WorkspaceValidator {

    public GlobalWorkspaceValidator() {
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }

    @Override
    public Mono<ValidationResult> beforeFinishReview(WorkspaceEntity workspace) {
        if (workspace.getStatus() != WorkspaceStatus.IN_REVIEW) {
            return Mono.just(
                    ValidationResult.builder()
                            .messages(new ValidationMessage("validation.workspace.cannot-finish-review-not-in-review"))
                            .build()
            );
        }

        return Mono.just(ValidationResult.EMPTY);
    }

    @Override
    public Mono<ValidationResult> beforePublish(WorkspaceEntity workspace) {
        if (workspace.getStatus() != WorkspaceStatus.INITIALIZED) {
            return Mono.just(
                    ValidationResult.builder()
                            .messages(new ValidationMessage("validation.workspace.cannot-publish-not-initialized"))
                            .build()
            );
        }

        return Mono.just(ValidationResult.EMPTY);
    }
}
