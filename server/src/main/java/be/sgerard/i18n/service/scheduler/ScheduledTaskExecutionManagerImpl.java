package be.sgerard.i18n.service.scheduler;

import be.sgerard.i18n.model.scheduler.ScheduledTaskExecution;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionSearchRequest;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import be.sgerard.i18n.repository.scheduler.ScheduledTaskExecutionRepository;
import be.sgerard.i18n.service.scheduler.listener.ScheduledTaskListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of the {@link ScheduledTaskExecutionManager execution manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class ScheduledTaskExecutionManagerImpl implements ScheduledTaskExecutionManager {

    private final ScheduledTaskExecutionRepository repository;
    private final ScheduledTaskListener listener;

    public ScheduledTaskExecutionManagerImpl(ScheduledTaskExecutionRepository repository, ScheduledTaskListener listener) {
        this.repository = repository;
        this.listener = listener;
    }

    @Override
    public Flux<ScheduledTaskExecutionEntity> find(ScheduledTaskExecutionSearchRequest request) {
        return repository.find(request);
    }

    @Override
    public Mono<ScheduledTaskExecutionEntity> notifyExecution(ScheduledTaskExecution execution) {
        return Mono
                .just(execution)
                .map(exec ->
                        new ScheduledTaskExecutionEntity(exec.getTaskDefinition())
                                .setStartTime(exec.getStartTime())
                                .setEndTime(exec.getEndTime())
                                .setStatus(exec.getStatus())
                                .setShortDescription(exec.getShortDescription())
                                .setDescription(exec.getDescription().orElse(null))
                )
                .flatMap(repository::save)
                .flatMap(exec -> listener.afterExecute(exec).thenReturn(exec));
    }

    @Override
    public Mono<Void> delete(ScheduledTaskExecutionEntity execution) {
        return Mono
                .just(execution)
                .flatMap(exec -> repository.delete(exec).thenReturn(exec))
                .flatMap(listener::afterDelete);
    }
}
