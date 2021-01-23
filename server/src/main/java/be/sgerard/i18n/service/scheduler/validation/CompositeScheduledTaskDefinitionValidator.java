package be.sgerard.i18n.service.scheduler.validation;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionPatchDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link ScheduledTaskDefinitionValidator task definition validator}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeScheduledTaskDefinitionValidator implements ScheduledTaskDefinitionValidator {

    private final List<ScheduledTaskDefinitionValidator> validators;

    @Lazy
    public CompositeScheduledTaskDefinitionValidator(@Autowired(required = false) List<ScheduledTaskDefinitionValidator> validators) {
        this.validators = validators;
    }

    @Override
    public Mono<ValidationResult> beforeCreateOrUpdate(ScheduledTaskDefinitionEntity taskDefinition) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeCreateOrUpdate(taskDefinition))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskDefinitionPatchDto patch) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeUpdate(taskDefinition, patch))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
