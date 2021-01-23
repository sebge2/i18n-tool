package be.sgerard.i18n.model.scheduler.snapshot;

import be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType;
import be.sgerard.i18n.model.scheduler.persistence.NonRecurringScheduledTaskTriggerEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;

/**
 * Dto for storing a {@link NonRecurringScheduledTaskTriggerEntity non-recurring scheduled task trigger} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@Getter
public class NonRecurringScheduledTaskTriggerSnapshotDto extends ScheduledTaskTriggerSnapshotDto {

    /**
     * @see NonRecurringScheduledTaskTriggerEntity#getStartTime()
     */
    private final Instant startTime;

    @JsonCreator
    public NonRecurringScheduledTaskTriggerSnapshotDto(@JsonProperty("startTime") Instant startTime) {
        super(ScheduledTaskTriggerType.NON_RECURRING);

        this.startTime = startTime;
    }
}
