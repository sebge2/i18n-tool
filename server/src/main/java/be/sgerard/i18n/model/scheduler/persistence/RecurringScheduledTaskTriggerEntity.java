package be.sgerard.i18n.model.scheduler.persistence;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType;
import be.sgerard.i18n.model.scheduler.dto.RecurringScheduledTaskTriggerDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskTriggerDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition.recurringTrigger;

/**
 * Recurring {@link ScheduledTaskTriggerEntity trigger} based on a CRON expression.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
public final class RecurringScheduledTaskTriggerEntity extends ScheduledTaskTriggerEntity {

    /**
     * Returns the {@link CronTrigger CRON trigger} based on the specified expression.
     */
    public static CronTrigger toCronTrigger(String cronExpression) {
        if(cronExpression == null){
            throw new IllegalArgumentException("The CRON expression cannot be null.");
        }

        return new CronTrigger(cronExpression);
    }

    /**
     * CRON expression specifying the periodicity of the task.
     */
    private String cronExpression;

    @PersistenceConstructor
    public RecurringScheduledTaskTriggerEntity() {
        super();
    }

    public RecurringScheduledTaskTriggerEntity(String cronExpression) {
        this();

        this.cronExpression = cronExpression;
    }

    @Override
    public ScheduledTaskTriggerType getType() {
        return ScheduledTaskTriggerType.RECURRING;
    }

    @Override
    public ScheduledFuture<?> planTask(Runnable task, TaskScheduler scheduler) {
        return scheduler.schedule(task, toCronTrigger(cronExpression));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Optional<Instant> getNextExecutionTime(Instant lastExecutionTime) {
        final Date lastCompletionTime = Optional.ofNullable(lastExecutionTime).map(Date::from).orElse(null);
        final SimpleTriggerContext triggerContext = new SimpleTriggerContext(null, null, lastCompletionTime);

        return Optional.of(
                toCronTrigger(cronExpression)
                        .nextExecutionTime(triggerContext)
                        .toInstant()
        );
    }

    @Override
    public ScheduledTaskDefinition.TaskTrigger toDefinition() {
        return recurringTrigger(getCronExpression());
    }

    @Override
    public RecurringScheduledTaskTriggerEntity updateFromDto(ScheduledTaskTriggerDto dto) {
        if (!(dto instanceof RecurringScheduledTaskTriggerDto)) {
            throw new IllegalArgumentException(String.format("The specified trigger is not a recurring trigger, but was %s.", dto.getType()));
        }

        setCronExpression(((RecurringScheduledTaskTriggerDto) dto).getCronExpression());

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

        final RecurringScheduledTaskTriggerEntity that = (RecurringScheduledTaskTriggerEntity) o;

        return Objects.equals(cronExpression, that.cronExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cronExpression);
    }

    @Override
    public String toString() {
        return String.format("Scheduled on %s.", getCronExpression());
    }
}
