package be.sgerard.i18n.model.scheduler.dto;

import be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType;
import be.sgerard.i18n.model.scheduler.persistence.NonRecurringScheduledTaskTriggerEntity;
import be.sgerard.i18n.model.scheduler.persistence.RecurringScheduledTaskTriggerEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskTriggerEntity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author Sebastien Gerard
 */
@Schema(name = "ScheduledTaskTrigger", description = "Trigger definition of a task.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NonRecurringScheduledTaskTriggerDto.class, name = "NON_RECURRING"),
        @JsonSubTypes.Type(value = RecurringScheduledTaskTriggerDto.class, name = "RECURRING")
})
@Getter
public abstract class ScheduledTaskTriggerDto {

    public static ScheduledTaskTriggerDto toDto(ScheduledTaskTriggerEntity trigger) {
        switch (trigger.getType()) {
            case RECURRING:
                final RecurringScheduledTaskTriggerEntity recurringTrigger = (RecurringScheduledTaskTriggerEntity) trigger;

                return new RecurringScheduledTaskTriggerDto(recurringTrigger.getCronExpression());
            case NON_RECURRING:
                final NonRecurringScheduledTaskTriggerEntity nonRecurringTrigger = (NonRecurringScheduledTaskTriggerEntity) trigger;

                return new NonRecurringScheduledTaskTriggerDto(nonRecurringTrigger.getStartTime());
            default:
                throw new UnsupportedOperationException(String.format("Unsupported type [%s].", trigger.getType()));
        }
    }

    @Schema(description = "The type of this trigger.", required = true)
    private final ScheduledTaskTriggerType type;

    protected ScheduledTaskTriggerDto(ScheduledTaskTriggerType type) {
        this.type = type;
    }
}
