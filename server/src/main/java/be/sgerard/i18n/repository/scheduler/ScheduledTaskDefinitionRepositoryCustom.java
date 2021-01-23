package be.sgerard.i18n.repository.scheduler;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinitionSearchRequest;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import reactor.core.publisher.Flux;

/**
 * Custom {@link ScheduledTaskDefinitionRepository task definition repository}.
 *
 * @author Sebastien Gerard
 */
public interface ScheduledTaskDefinitionRepositoryCustom {

    /**
     * Searches {@link ScheduledTaskDefinitionEntity task definitions} satisfying the specified {@link ScheduledTaskDefinitionSearchRequest request}.
     */
    Flux<ScheduledTaskDefinitionEntity> find(ScheduledTaskDefinitionSearchRequest request);
}
