package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionResult;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionSearchRequest;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.service.scheduler.ScheduledTaskExecutionManager;
import be.sgerard.i18n.service.scheduler.ScheduledTaskManager;
import be.sgerard.test.i18n.mock.TestableScheduledTaskExecutor;
import junit.framework.AssertionFailedError;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition.nonRecurringTrigger;
import static be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition.recurringTrigger;

/**
 * Helper for testing scheduled tasks.
 *
 * @author Sebastien Gerard
 */
@Component
public class ScheduledTaskTestHelper {

    public static final String DEFAULT_TASK_ID = "testable-scheduled-task";
    public static final int WAIT_DURATION_IN_MS = 50;

    public static ScheduledTaskDefinition sampleRecurringTask() {
        return ScheduledTaskDefinition.builder()
                .id(DEFAULT_TASK_ID)
                .trigger(recurringTrigger("* * * * * *"))
                .name(new LocalizedString(Locale.ENGLISH, "Testable Task"))
                .description(new LocalizedString(Locale.ENGLISH, "Task used for testing purpose."))
                .build();
    }

    public static ScheduledTaskDefinition sampleNonRecurringTask() {
        return ScheduledTaskDefinition.builder()
                .id(DEFAULT_TASK_ID)
                .trigger(nonRecurringTrigger(Instant.now().plus(1, ChronoUnit.HOURS)))
                .name(new LocalizedString(Locale.ENGLISH, "Testable Task"))
                .description(new LocalizedString(Locale.ENGLISH, "Task used for testing purpose."))
                .build();
    }

    private final ScheduledTaskManager taskManager;
    private final ScheduledTaskExecutionManager executionManager;
    private final TestableScheduledTaskExecutor executor;

    private final Set<String> taskDefinitionIds = new HashSet<>();

    public ScheduledTaskTestHelper(ScheduledTaskManager taskManager,
                                   ScheduledTaskExecutionManager executionManager,
                                   TestableScheduledTaskExecutor executor) {
        this.taskManager = taskManager;
        this.executionManager = executionManager;
        this.executor = executor;
    }

    public TaskStep createOrUpdate(ScheduledTaskDefinition taskDefinition, ScheduledTaskExecutionResult result) {
        executor.register(taskDefinition.getId(), result);

        final ScheduledTaskDefinitionEntity entity = taskManager
                .createOrUpdate(taskDefinition)
                .block();

        return new TaskStep(entity);
    }

    public TaskStep createOrUpdate(ScheduledTaskDefinition taskDefinition) {
        return createOrUpdate(taskDefinition, createDefaultResult());
    }

    public TaskStep forTask(ScheduledTaskDefinitionEntity taskDefinition) {
        return new TaskStep(taskDefinition);
    }

    private ScheduledTaskExecutionResult createDefaultResult() {
        return ScheduledTaskExecutionResult
                .builder()
                .description(new LocalizedString(Locale.ENGLISH, "Task executed successfully."))
                .build();
    }

    @SuppressWarnings("UnusedReturnValue")
    public ScheduledTaskTestHelper deleteAll() {
        taskManager
                .findAll()
                .filter(taskDefinition -> taskDefinitionIds.contains(taskDefinition.getId()))
                .map(ScheduledTaskDefinitionEntity::getId)
                .flatMap(taskManager::delete)
                .blockLast();

        return this;
    }

    public final class TaskStep {

        private final ScheduledTaskDefinitionEntity taskDefinition;

        public TaskStep(ScheduledTaskDefinitionEntity taskDefinition) {
            this.taskDefinition = taskDefinition;
            taskDefinitionIds.add(taskDefinition.getId());
        }

        public ScheduledTaskTestHelper and() {
            return ScheduledTaskTestHelper.this;
        }

        public ScheduledTaskDefinitionEntity get() {
            return this.taskDefinition;
        }

        public TaskStep disable() {
            final ScheduledTaskDefinitionEntity entity = taskManager
                    .disable(taskDefinition.getId())
                    .block();

            executor.unregister(taskDefinition.getId());

            return new TaskStep(entity);
        }

        public TaskStep enable() {
            final ScheduledTaskDefinitionEntity entity = taskManager
                    .enable(taskDefinition.getId())
                    .block();

            executor.unregister(taskDefinition.getId());

            return new TaskStep(entity);
        }

        public TaskStep waitForExecution() {
            final ScheduledTaskExecutionSearchRequest searchRequest = ScheduledTaskExecutionSearchRequest.builder()
                    .taskDefinitionId(taskDefinition.getId())
                    .build();

            final boolean found = Flux
                    .interval(Duration.ofMillis(WAIT_DURATION_IN_MS))
                    .take(100)
                    .flatMap(i -> executionManager.find(searchRequest))
                    .hasElements()
                    .blockOptional()
                    .orElse(false);

            if (!found) {
                throw new AssertionFailedError("No execution found for the current scheduled task.");
            }

            return this;
        }

        public ScheduledTaskTestHelper delete() {
            taskManager
                    .delete(taskDefinition.getId())
                    .block();

            return ScheduledTaskTestHelper.this;
        }
    }
}
