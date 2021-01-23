package be.sgerard.i18n.repository.scheduler;

import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * {@link ReactiveMongoRepository Repository} of {@link ScheduledTaskDefinitionEntity scheduled task definitions}.
 *
 * @author Sebastien Gerard
 */
public interface ScheduledTaskDefinitionRepository extends ReactiveMongoRepository<ScheduledTaskDefinitionEntity, String>, ScheduledTaskDefinitionRepositoryCustom {

    /**
     * @see ScheduledTaskDefinitionEntity#getId()
     */
    String FIELD_ID = "id";

    /**
     * @see ScheduledTaskDefinitionEntity#isEnabled()
     */
    String FIELD_ENABLED = "enabled";

    /**
     * @see ScheduledTaskDefinitionEntity#getLastExecutionTime()
     */
    String FIELD_LAST_EXECUTION_TIME = "lastExecutionTime";

    /**
     * Finds the task definition by its {@link ScheduledTaskDefinitionEntity#getInternalId() internal id}.
     */
    Mono<ScheduledTaskDefinitionEntity> findByInternalId(String taskDefinitionInternalId);
}
