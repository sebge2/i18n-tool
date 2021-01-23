package be.sgerard.i18n.repository.scheduler;

import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionSearchRequest;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import reactor.core.publisher.Flux;

/**
 * Custom {@link ScheduledTaskExecutionRepository task execution repository}.
 *
 * @author Sebastien Gerard
 */
public interface ScheduledTaskExecutionRepositoryCustom {

    /**
     * Searches {@link ScheduledTaskExecutionEntity task executions} satisfying the specified {@link ScheduledTaskExecutionSearchRequest request}.
     */
    Flux<ScheduledTaskExecutionEntity> find(ScheduledTaskExecutionSearchRequest request);

}
