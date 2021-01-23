package be.sgerard.i18n.service.scheduler.validation;

import be.sgerard.i18n.model.scheduler.dto.RecurringScheduledTaskTriggerDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionPatchDto;
import be.sgerard.i18n.model.scheduler.persistence.RecurringScheduledTaskTriggerEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link ScheduledTaskDefinitionValidator Task definition validator} checking recurring tasks: the CRON expression must be valid.
 *
 * @author Sebastien Gerard
 */
@Component
public class RecurringScheduledTaskDefinitionValidator implements ScheduledTaskDefinitionValidator {

    /**
     * Validation message key specifying that the trigger expression is invalid.
     */
    public static final String TRIGGER_CRON_INVALID = "validation.scheduled-task-definition.trigger-cron-invalid";

    private static final Logger logger = LoggerFactory.getLogger(RecurringScheduledTaskDefinitionValidator.class);

    public RecurringScheduledTaskDefinitionValidator() {
    }

    @Override
    public Mono<ValidationResult> beforeCreateOrUpdate(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono
                .just(taskDefinition)
                .map(ScheduledTaskDefinitionEntity::getTrigger)
                .filter(RecurringScheduledTaskTriggerEntity.class::isInstance)
                .map(RecurringScheduledTaskTriggerEntity.class::cast)
                .map(trigger -> validateCronExpression(trigger.getCronExpression()))
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskDefinitionPatchDto patch) {
        return Mono.just(
                validateCronExpression(patch)
        );
    }

    /**
     * Validates that the CRON expression (if specified by the patch) is valid.
     */
    private ValidationResult validateCronExpression(ScheduledTaskDefinitionPatchDto patch) {
        return patch
                .getTrigger()
                .filter(RecurringScheduledTaskTriggerDto.class::isInstance)
                .map(RecurringScheduledTaskTriggerDto.class::cast)
                .map(recurringTrigger -> validateCronExpression(recurringTrigger.getCronExpression()))
                .orElse(ValidationResult.EMPTY);
    }

    /**
     * Validates that the specified CRON expression is valid.
     */
    private ValidationResult validateCronExpression(String cronExpression) {
        try {
            RecurringScheduledTaskTriggerEntity.toCronTrigger(cronExpression);

            return ValidationResult.EMPTY;
        } catch (Exception e) {
            logger.debug(String.format("Error while parsing expression [%s].", cronExpression), e);

            return ValidationResult.singleMessage(new ValidationMessage(TRIGGER_CRON_INVALID, cronExpression));
        }
    }
}
