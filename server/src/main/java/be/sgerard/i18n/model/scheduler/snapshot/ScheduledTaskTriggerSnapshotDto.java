package be.sgerard.i18n.model.scheduler.snapshot;

import be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskTriggerEntity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

/**
 * Dto for storing a {@link ScheduledTaskTriggerEntity scheduled task trigger} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NonRecurringScheduledTaskTriggerSnapshotDto.class, name = "NON_RECURRING"),
        @JsonSubTypes.Type(value = RecurringScheduledTaskTriggerSnapshotDto.class, name = "RECURRING")
})
@Getter
public abstract class ScheduledTaskTriggerSnapshotDto {

    /**
     * @see ScheduledTaskTriggerEntity#getType()
     */
    private final ScheduledTaskTriggerType type;

    protected ScheduledTaskTriggerSnapshotDto(ScheduledTaskTriggerType type) {
        this.type = type;
    }
}
