package be.sgerard.i18n.service.scheduler.validation;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionPatchDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Validator of the lifecycle of {@link ScheduledTaskDefinitionEntity scheduled task definition}.
 *
 * @author Sebastien Gerard
 */
public interface ScheduledTaskDefinitionValidator {

    /**
     * Validates before creating, or updating the specified task definition.
     */
    default Mono<ValidationResult> beforeCreateOrUpdate(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates before updating the specified task definition.
     */
    default Mono<ValidationResult> beforeUpdate(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskDefinitionPatchDto patch) {
        return Mono.just(ValidationResult.EMPTY);
    }
}
