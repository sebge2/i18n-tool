package be.sgerard.i18n.service.scheduler.snapshot;

import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.scheduler.ScheduledTaskExecutionRepository;
import be.sgerard.i18n.service.snapshot.BaseSnapshotHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.OperationNotSupportedException;

/**
 * {@link BaseSnapshotHandler Snapshot handler} for {@link ScheduledTaskExecutionEntity scheduled task execution}. This handler
 * is particular because it does not export/import executions.
 *
 * @author Sebastien Gerard
 */
@Component
public class ScheduledTaskExecutionSnapshotHandler extends BaseSnapshotHandler<ScheduledTaskExecutionEntity, Object> {

    /**
     * Name of the file containing users.
     */
    public static final String FILE = "scheduled_task_execution.json";

    public ScheduledTaskExecutionSnapshotHandler(ObjectMapper objectMapper,
                                                 ScheduledTaskExecutionRepository repository) {
        super(FILE, Object.class, objectMapper, repository);
    }

    @Override
    public int getImportPriority() {
        return 61;
    }

    @Override
    protected Mono<ValidationResult> validate(ScheduledTaskExecutionEntity taskDefinition) {
        return Mono.just(ValidationResult.EMPTY);
    }

    @Override
    protected Flux<ScheduledTaskExecutionEntity> findAll() {
        return Flux.empty();
    }

    @Override
    protected Mono<ScheduledTaskExecutionEntity> mapFromDto(Object dto) {
        return Mono.error(OperationNotSupportedException::new);
    }

    @Override
    protected Mono<Object> mapToDto(ScheduledTaskExecutionEntity taskDefinition) {
        return Mono.error(OperationNotSupportedException::new);
    }
}
