package be.sgerard.i18n.service.scheduler;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecution;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionResult;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionStatus;
import be.sgerard.i18n.service.scheduler.executor.ScheduledTaskExecutor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * Scheduler of {@link ScheduledTaskDefinitionEntity tasks}.
 *
 * @author Sebastien Gerard
 */
class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private final TaskScheduler taskScheduler;
    private final ScheduledTaskExecutor taskExecutor;
    private final Callback callback;

    private final List<ScheduledTask> scheduledTasks = new ArrayList<>();

    Scheduler(TaskScheduler taskScheduler, ScheduledTaskExecutor taskExecutor, Callback callback) {
        this.taskExecutor = taskExecutor;
        this.taskScheduler = taskScheduler;
        this.callback = callback;
    }

    /**
     * Returns all {@link ScheduledTaskDefinitionEntity definitions} of scheduled tasks.
     */
    public Flux<ScheduledTaskDefinitionEntity> getAllScheduled() {
        return Flux
                .fromIterable(scheduledTasks)
                .map(ScheduledTask::getRunnable)
                .map(ScheduledTaskWrapper::getTaskDefinition);
    }

    /**
     * Schedules the specified {@link ScheduledTaskDefinitionEntity task}, it will be added for execution.
     */
    public Mono<Void> schedule(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono
                .just(taskDefinition)
                .flatMap(this::findScheduledTask)
                .flatMap(scheduledTask -> unregisterScheduledTaskIfLegacy(scheduledTask, taskDefinition))
                .switchIfEmpty(createScheduledTaskIfNeeded(taskDefinition))
                .flatMap(ScheduledTask::register)
                .then();
    }

    /**
     * Un-schedules the specified {@link ScheduledTaskDefinition task}.
     */
    public Mono<Void> unschedule(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono
                .just(taskDefinition)
                .flatMap(this::findScheduledTask)
                .flatMap(ScheduledTask::unregister)
                .then();
    }

    /**
     * Finds the {@link ScheduledTask task} associated to the specified {@link ScheduledTaskDefinitionEntity definition}?
     */
    private Mono<ScheduledTask> findScheduledTask(ScheduledTaskDefinitionEntity taskDefinition) {
        return Flux.fromIterable(scheduledTasks)
                .filter(scheduledTask -> scheduledTask.isAssociatedToTask(taskDefinition))
                .next();
    }

    /**
     * Unregisters the specified {@link ScheduledTask scheduled task} if it has no more the same scheduling as the specified task.
     * If they have the same scheduling, this task is returned.
     */
    private Mono<ScheduledTask> unregisterScheduledTaskIfLegacy(ScheduledTask scheduledTask, ScheduledTaskDefinitionEntity taskDefinition) {
        if (scheduledTask.hasSameScheduling(taskDefinition)) {
            return Mono.just(scheduledTask);
        } else {
            return scheduledTask
                    .unregister()
                    .then(Mono.empty());
        }
    }

    /**
     * Creates the {@link ScheduledTask task} associated to the specified {@link ScheduledTaskDefinitionEntity definition}.
     */
    private Mono<ScheduledTask> createScheduledTaskIfNeeded(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono.fromSupplier(() -> {
            if (taskDefinition.isCompleted()) {
                logger.info("The task [{}] ({}) based on [{}] is not scheduled since, it's completed.", taskDefinition.getInternalId(), taskDefinition.getId(), taskDefinition.getTrigger());

                return null;
            }

            logger.info("The task [{}] ({}) is now scheduled based on [{}].", taskDefinition.getInternalId(), taskDefinition.getId(), taskDefinition.getTrigger());

            return new ScheduledTask(new ScheduledTaskWrapper(taskDefinition));
        });
    }

    /**
     * Callback for following scheduling events.
     */
    public interface Callback {

        /**
         * Performs an action when the specified task has been executed.
         */
        Mono<ScheduledTaskExecutionEntity> onExecutedTask(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskExecution execution);

    }

    /**
     * Task scheduled in the {@link TaskScheduler task scheduler}.
     */
    private final class ScheduledTask {

        private final ScheduledTaskWrapper runnable;
        private ScheduledFuture<?> future;

        private ScheduledTask(ScheduledTaskWrapper runnable) {
            this.runnable = runnable;
            this.runnable.setScheduledTask(this);
        }

        /**
         * Returns the {@link ScheduledTaskWrapper runnable} to be executed.
         */
        public ScheduledTaskWrapper getRunnable() {
            return runnable;
        }

        /**
         * Returns the {@link ScheduledFuture future} of this scheduled task.
         */
        public ScheduledFuture<?> getFuture() {
            return future;
        }

        /**
         * Registers the task for execution.
         */
        public Mono<ScheduledTask> register() {
            if (future != null) {
                return Mono.just(this);
            }

            return Mono
                    .just(this)
                    .doOnNext(task -> {
                        future = getTaskDefinition().getTrigger().planTask(getRunnable(), taskScheduler);
                        scheduledTasks.add(task);
                    })
                    .doOnNext(task ->
                            logger.info("Scheduled task added [{}] ({}). Number scheduled tasks {}.",
                                    task.getTaskDefinition().getInternalId(), task.getTaskDefinition().getId(), scheduledTasks.size())
                    );
        }

        /**
         * Unregisters the future execution of this task.
         */
        public Mono<ScheduledTask> unregister() {
            return Mono
                    .just(this)
                    .doOnNext(task -> {
                        future.cancel(false);
                        scheduledTasks.remove(task);
                    })
                    .doOnNext(task -> logger.info("Scheduled task removed [{}] ({}). Number scheduled tasks {}.", task.getTaskDefinition().getInternalId(), task.getTaskDefinition().getId(), scheduledTasks.size()));
        }

        /**
         * Returns the {@link ScheduledTaskDefinitionEntity definition} of the associated task.
         */
        public ScheduledTaskDefinitionEntity getTaskDefinition() {
            return getRunnable().getTaskDefinition();
        }

        /**
         * Returns whether the specified {@link ScheduledTaskDefinitionEntity task definition} is associated to this scheduled task.
         */
        public boolean isAssociatedToTask(ScheduledTaskDefinitionEntity taskDefinition) {
            return Objects.equals(getTaskDefinition().getId(), taskDefinition.getId());
        }

        /**
         * Returns whether the specified {@link ScheduledTaskDefinitionEntity task definition} has the same scheduling configuration
         * as this task.
         */
        public boolean hasSameScheduling(ScheduledTaskDefinitionEntity taskDefinition) {
            return Objects.equals(getTaskDefinition().getTrigger(), taskDefinition.getTrigger());
        }

        /**
         * Returns whether this task is recurring.
         */
        public boolean isRecurring() {
            return getTaskDefinition().getTrigger().getType().isRecurring();
        }
    }

    /**
     * The {@link Runnable runnable} to be executed.
     */
    @Getter
    @Setter
    private final class ScheduledTaskWrapper implements Runnable {

        /**
         * The {@link ScheduledTaskDefinitionEntity definition} of the task to execute.
         */
        private final ScheduledTaskDefinitionEntity taskDefinition;

        /**
         * The associated {@link ScheduledTask scheduled task}.
         */
        private ScheduledTask scheduledTask;

        private ScheduledTaskWrapper(ScheduledTaskDefinitionEntity taskDefinition) {
            this.taskDefinition = taskDefinition;
        }

        @Override
        public void run() {
            Mono
                    .just(taskDefinition)
                    .doOnNext(taskDefinition -> logger.info("Executing task [{}] ({}).", taskDefinition.getInternalId(), taskDefinition.getId()))
                    .flatMap(this::execute)
                    .doOnNext(execution ->
                            logger.info("Execution of task [{}] ({}) finished in {} ms.",
                                    execution.getTaskDefinition().getInternalId(), execution.getTaskDefinition().getId(), execution.getDuration().toMillis())
                    )
                    .flatMap(execution -> callback.onExecutedTask(getTaskDefinition(), execution))
                    .flatMap(this::unregisterIfNonRecurring)
                    .subscribe();
        }

        /**
         * Executes the specified {@link ScheduledTaskDefinitionEntity task}.
         */
        private Mono<ScheduledTaskExecution> execute(ScheduledTaskDefinitionEntity taskDefinition) {
            final Instant startTime = Instant.now();

            return taskExecutor
                    .executeTask(taskDefinition.getInternalId())
                    .switchIfEmpty(Mono.defer(this::handleNoResult))
                    .onErrorResume(error -> handleError(error, taskDefinition))
                    .map(result -> createExecution(result, taskDefinition, startTime));
        }

        /**
         * Handles the case when nothing has been returned by the executor.
         */
        private Mono<ScheduledTaskExecutionResult> handleNoResult() {
            return Mono.just(ScheduledTaskExecutionResult.builder().build());
        }

        /**
         * Handles the specified error and returns the right {@link ScheduledTaskExecutionResult result}.
         */
        private Mono<ScheduledTaskExecutionResult> handleError(Throwable error, ScheduledTaskDefinitionEntity taskDefinition) {
            logger.warn(String.format("Execution of task [%s] failed.", taskDefinition.getId()), error);

            return Mono.just(
                    ScheduledTaskExecutionResult.builder()
                            .status(ScheduledTaskExecutionStatus.FAILED)
                            .description(LocalizedString.fromBundle("i18n/misc", "scheduled-task.failure-result-msg", error.getMessage()))
                            .build()
            );
        }

        /**
         * Creates a new {@link ScheduledTaskExecution execution} for keeping track of the execution of the specified
         * task started at the specified time and that produced the specified result.
         */
        private ScheduledTaskExecution createExecution(ScheduledTaskExecutionResult result,
                                                       ScheduledTaskDefinitionEntity taskDefinition,
                                                       Instant startTime) {
            return ScheduledTaskExecution.builder()
                    .taskDefinition(taskDefinition)
                    .startTime(startTime)
                    .endTime(Instant.now())
                    .status(result.getStatus())
                    .shortDescription(result.getShortDescription())
                    .description(result.getDescription().orElse(null))
                    .build();
        }

        /**
         * Unregisters the task if this task is non-recurring and won't be executed anymore.
         */
        private Mono<ScheduledTaskExecutionEntity> unregisterIfNonRecurring(ScheduledTaskExecutionEntity execution) {
            if (getScheduledTask().getTaskDefinition().getTrigger().getNextExecutionTime(execution.getStartTime()).isPresent()) {
                return Mono.just(execution);
            } else {
                return getScheduledTask()
                        .unregister()
                        .thenReturn(execution);
            }
        }
    }
}
