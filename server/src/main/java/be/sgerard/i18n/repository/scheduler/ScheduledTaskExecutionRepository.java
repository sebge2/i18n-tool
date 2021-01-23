package be.sgerard.i18n.repository.scheduler;

import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * {@link ReactiveMongoRepository Repository} of {@link ScheduledTaskExecutionEntity scheduled task executions}.
 *
 * @author Sebastien Gerard
 */
public interface ScheduledTaskExecutionRepository extends ReactiveMongoRepository<ScheduledTaskExecutionEntity, String>, ScheduledTaskExecutionRepositoryCustom {

    /**
     * @see ScheduledTaskExecutionEntity#getStartTime()
     */
    String FIELD_START_TIME = "startTime";

    /**
     * @see ScheduledTaskExecutionEntity#getDefinitionId()
     */
    String FIELD_DEFINITION_ID = "definitionId";

    /**
     * @see ScheduledTaskExecutionEntity#getStatus()
     */
    String FIELD_STATUS = "status";
}
