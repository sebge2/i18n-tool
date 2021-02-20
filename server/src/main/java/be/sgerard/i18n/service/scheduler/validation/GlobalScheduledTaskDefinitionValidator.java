package be.sgerard.i18n.service.scheduler.validation;

import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionPatchDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link ScheduledTaskDefinitionValidator Task definition validator} checking global information.
 *
 * @author Sebastien Gerard
 */
@Component
public class GlobalScheduledTaskDefinitionValidator implements ScheduledTaskDefinitionValidator {

    /**
     * Validation message key specifying that the trigger type of a scheduled task changed.
     */
    public static final String TRIGGER_TYPE_CHANGED = "validation.scheduled-task-definition.trigger-type-changed";

    public GlobalScheduledTaskDefinitionValidator() {
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskDefinitionPatchDto patch) {
        return Mono.just(
                validateTriggerTypeNotChanged(taskDefinition, patch)
        );
    }

    /**
     * Validates that the specified trigger type did not changed.
     */
    private ValidationResult validateTriggerTypeNotChanged(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskDefinitionPatchDto patch) {
        if (hasTriggerTypeChanged(taskDefinition, patch)) {
            return ValidationResult.singleMessage(new ValidationMessage(TRIGGER_TYPE_CHANGED, taskDefinition.getTrigger().getType()));
        } else {
            return ValidationResult.EMPTY;
        }
    }

    /**
     * Returns whether the trigger type changed between the original and the patched one.
     */
    private boolean hasTriggerTypeChanged(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskDefinitionPatchDto patch) {
        return patch.getTrigger()
                .map(triggerPatch -> triggerPatch.getType() != taskDefinition.getTrigger().getType())
                .orElse(false);
    }
}
