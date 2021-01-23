package be.sgerard.i18n.model.scheduler.snapshot;

import be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType;
import be.sgerard.i18n.model.scheduler.persistence.RecurringScheduledTaskTriggerEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Dto for storing a {@link RecurringScheduledTaskTriggerEntity recurring scheduled task trigger} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@Getter
public class RecurringScheduledTaskTriggerSnapshotDto extends ScheduledTaskTriggerSnapshotDto {

    /**
     * @see RecurringScheduledTaskTriggerEntity#getCronExpression()
     */
    private final String cronExpression;

    @JsonCreator
    public RecurringScheduledTaskTriggerSnapshotDto(@JsonProperty("cronExpression") String cronExpression) {
        super(ScheduledTaskTriggerType.RECURRING);

        this.cronExpression = cronExpression;
    }
}
