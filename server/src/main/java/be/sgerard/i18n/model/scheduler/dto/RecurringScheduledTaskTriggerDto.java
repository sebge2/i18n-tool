package be.sgerard.i18n.model.scheduler.dto;

import be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Recurring {@link ScheduledTaskTriggerDto trigger} based on a CRON expression.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "RecurringScheduledTaskTrigger", description = "Recurring trigger based on a CRON expression.")
@Getter
public class RecurringScheduledTaskTriggerDto extends ScheduledTaskTriggerDto {

    @Schema(description = "CRON expression specifying the periodicity of the task.", required = true)
    private final String cronExpression;

    @JsonCreator
    public RecurringScheduledTaskTriggerDto(@JsonProperty("cronExpression") String cronExpression) {
        super(ScheduledTaskTriggerType.RECURRING);

        this.cronExpression = cronExpression;
    }
}
