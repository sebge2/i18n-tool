package be.sgerard.i18n.service.scheduler.validation;

import be.sgerard.i18n.model.scheduler.dto.NonRecurringScheduledTaskTriggerDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionPatchDto;
import be.sgerard.i18n.model.scheduler.persistence.NonRecurringScheduledTaskTriggerEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * {@link ScheduledTaskDefinitionValidator Task definition validator} checking recurring tasks: the time must be in the future.
 *
 * @author Sebastien Gerard
 */
@Component
public class NonRecurringScheduledTaskDefinitionValidator implements ScheduledTaskDefinitionValidator {

    /**
     * Validation message key specifying that the trigger start time is invalid.
     */
    public static final String TRIGGER_START_TIME_INVALID = "validation.scheduled-task-definition.start-time-invalid";

    /**
     * The start time must be at least X ms in the future.
     */
    public static final long MINIMUM_FUTURE_TIME_IN_MS = 60000; // 1min

    public NonRecurringScheduledTaskDefinitionValidator() {
    }

    @Override
    public Mono<ValidationResult> beforeCreateOrUpdate(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono
                .just(taskDefinition)
                .map(ScheduledTaskDefinitionEntity::getTrigger)
                .filter(NonRecurringScheduledTaskTriggerEntity.class::isInstance)
                .map(NonRecurringScheduledTaskTriggerEntity.class::cast)
                .map(trigger -> validateStartTime(trigger.getStartTime()))
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskDefinitionPatchDto patch) {
        return Mono.just(validateStartTime(patch));
    }

    /**
     * Validates that the start time is valid.
     */
    private ValidationResult validateStartTime(ScheduledTaskDefinitionPatchDto patch) {
        return patch
                .getTrigger()
                .filter(NonRecurringScheduledTaskTriggerDto.class::isInstance)
                .map(NonRecurringScheduledTaskTriggerDto.class::cast)
                .map(recurringTrigger -> validateStartTime(recurringTrigger.getStartTime()))
                .orElse(ValidationResult.EMPTY);
    }

    /**
     * Validates that the start time is valid.
     */
    private ValidationResult validateStartTime(Instant startTime) {
        if (startTime == null) {
            return ValidationResult.singleMessage(new ValidationMessage(TRIGGER_START_TIME_INVALID));
        }

        return ValidationResult.EMPTY;
    }
}
