package be.sgerard.i18n.model.scheduler;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;

/**
 * Definition of a scheduled task.
 *
 * @author Sebastien Gerard
 */
@Getter
@Builder(builderClassName = "Builder", toBuilder = true)
@ToString(of = {"id"})
public class ScheduledTaskDefinition {

    /**
     * @see RecurringTaskTrigger
     */
    public static RecurringTaskTrigger recurringTrigger(String cronExpression){
        return new RecurringTaskTrigger(cronExpression);
    }

    /**
     * @see NonRecurringTaskTrigger
     */
    public static NonRecurringTaskTrigger nonRecurringTrigger(Instant startTime){
        return new NonRecurringTaskTrigger(startTime);
    }

    /**
     * Unique id of this kind of task.
     */
    private final String id;

    /**
     * Name of this kind of task (to be displayed to the end-user).
     */
    private final LocalizedString name;

    /**
     * Description of this kind of task (to be displayed to the end-user).
     */
    private final LocalizedString description;

    /**
     * {@link TaskTrigger Trigger} definition: when the task must be executed.
     */
    private final TaskTrigger trigger;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ScheduledTaskDefinition that = (ScheduledTaskDefinition) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Trigger definition of a task.
     */
    public interface TaskTrigger {

        /**
         * Returns the {@link ScheduledTaskTriggerType type} of this trigger.
         */
        ScheduledTaskTriggerType getType();

    }

    /**
     * Recurring {@link TaskTrigger trigger} based on a CRON expression.
     */
    @Getter
    public static final class RecurringTaskTrigger implements TaskTrigger {

        /**
         * CRON expression specifying the periodicity of the task.
         */
        private final String cronExpression;

        public RecurringTaskTrigger(String cronExpression) {
            this.cronExpression = cronExpression;
        }

        @Override
        public ScheduledTaskTriggerType getType() {
            return ScheduledTaskTriggerType.RECURRING;
        }
    }

    /**
     * Non-recurring {@link TaskTrigger trigger} asking the single execution of a task on a certain date.
     */
    @Getter
    public static final class NonRecurringTaskTrigger implements TaskTrigger {

        /**
         * Date when the task must be executed.
         */
        private final Instant startTime;

        public NonRecurringTaskTrigger(Instant startTime) {
            this.startTime = startTime;
        }

        @Override
        public ScheduledTaskTriggerType getType() {
            return ScheduledTaskTriggerType.NON_RECURRING;
        }
    }
}
