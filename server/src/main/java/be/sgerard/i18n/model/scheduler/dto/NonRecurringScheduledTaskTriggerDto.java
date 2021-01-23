package be.sgerard.i18n.model.scheduler.dto;

import be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.Instant;

/**
 * Non-recurring {@link ScheduledTaskTriggerDto trigger} asking the single execution of a task on a certain date.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "NonRecurringScheduledTaskTrigger", description = "Non-recurring trigger asking the single execution of a task on a certain date.")
@Getter
public class NonRecurringScheduledTaskTriggerDto extends ScheduledTaskTriggerDto {

    @Schema(description = "Date when the task must be executed.", required = true)
    private final Instant startTime;

    @JsonCreator
    public NonRecurringScheduledTaskTriggerDto(@JsonProperty("startTime") Instant startTime) {
        super(ScheduledTaskTriggerType.NON_RECURRING);

        this.startTime = startTime;
    }
}
