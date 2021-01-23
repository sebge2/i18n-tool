package be.sgerard.i18n.model.scheduler.persistence;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType;
import be.sgerard.i18n.model.scheduler.dto.NonRecurringScheduledTaskTriggerDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskTriggerDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition.nonRecurringTrigger;

/**
 * Non-recurring {@link ScheduledTaskTriggerEntity trigger} asking the single execution of a task on a certain date.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
public final class NonRecurringScheduledTaskTriggerEntity extends ScheduledTaskTriggerEntity {

    /**
     * Date when the task must be executed.
     */
    private Instant startTime;

    @PersistenceConstructor
    public NonRecurringScheduledTaskTriggerEntity() {
        super();
    }

    public NonRecurringScheduledTaskTriggerEntity(Instant startTime) {
        this();

        this.startTime = startTime;
    }

    @Override
    public ScheduledTaskTriggerType getType() {
        return ScheduledTaskTriggerType.NON_RECURRING;
    }

    @Override
    public ScheduledFuture<?> planTask(Runnable task, TaskScheduler scheduler) {
        return scheduler.schedule(task, startTime);
    }

    @Override
    public Optional<Instant> getNextExecutionTime(Instant lastExecutionTime) {
        return Optional.ofNullable(
                isAlreadyExecuted(lastExecutionTime)
                        ? null
                        : getStartTime()
        );
    }

    @Override
    public ScheduledTaskDefinition.TaskTrigger toDefinition() {
        return nonRecurringTrigger(getStartTime());
    }

    @Override
    public NonRecurringScheduledTaskTriggerEntity updateFromDto(ScheduledTaskTriggerDto dto) {
        if (!(dto instanceof NonRecurringScheduledTaskTriggerDto)) {
            throw new IllegalArgumentException(String.format("The specified trigger is not a non-recurring trigger, but was %s.", dto.getType()));
        }

        setStartTime(((NonRecurringScheduledTaskTriggerDto) dto).getStartTime());

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final NonRecurringScheduledTaskTriggerEntity that = (NonRecurringScheduledTaskTriggerEntity) o;

        return Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime);
    }

    @Override
    public String toString() {
        return String.format("Scheduled on '%s'.", getStartTime());
    }

    /**
     * Returns whether the task has been already executed and should not be executed anymore.
     */
    private boolean isAlreadyExecuted(Instant lastExecutionTime) {
        return (lastExecutionTime != null) && ((lastExecutionTime.isAfter(getStartTime())) || Objects.equals(lastExecutionTime, getStartTime()));
    }
}
