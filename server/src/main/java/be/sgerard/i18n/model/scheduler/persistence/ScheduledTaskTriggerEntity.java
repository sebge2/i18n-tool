package be.sgerard.i18n.model.scheduler.persistence;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskTriggerDto;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

/**
 * Trigger definition of a task.
 *
 * @author Sebastien Gerard
 */
@Getter
public abstract class ScheduledTaskTriggerEntity {

    /**
     * Creates the {@link ScheduledTaskTriggerEntity entity} from the {@link ScheduledTaskDefinition task definition}.
     */
    public static ScheduledTaskTriggerEntity fromDefinition(ScheduledTaskDefinition taskDefinition) {
        final ScheduledTaskDefinition.TaskTrigger trigger = taskDefinition.getTrigger();
        switch (trigger.getType()) {
            case NON_RECURRING:
                return new NonRecurringScheduledTaskTriggerEntity(((ScheduledTaskDefinition.NonRecurringTaskTrigger) trigger).getStartTime());
            case RECURRING:
                return new RecurringScheduledTaskTriggerEntity(((ScheduledTaskDefinition.RecurringTaskTrigger) trigger).getCronExpression());
            default:
                throw new UnsupportedOperationException(String.format("Unsupported type [%s].", trigger.getType()));
        }
    }

    protected ScheduledTaskTriggerEntity() {
    }

    /**
     * The {@link ScheduledTaskTriggerType type} of this trigger.
     */
    @Field(name = "type")
    public abstract ScheduledTaskTriggerType getType();

    /**
     * Plans the task for execution using the specified {@link TaskScheduler scheduler}.
     */
    public abstract ScheduledFuture<?> planTask(Runnable task, TaskScheduler scheduler);

    /**
     * Returns the time when the next execution will occur knowing the last execution time.
     */
    public abstract Optional<Instant> getNextExecutionTime(Instant lastExecutionTime);

    /**
     * Returns the original {@link ScheduledTaskDefinition.TaskTrigger definition} of this entity.
     */
    public abstract ScheduledTaskDefinition.TaskTrigger toDefinition();

    /**
     * Updates the current trigger from its {@link ScheduledTaskTriggerDto DTO} representation.
     */
    public abstract ScheduledTaskTriggerEntity updateFromDto(ScheduledTaskTriggerDto dto);

}
