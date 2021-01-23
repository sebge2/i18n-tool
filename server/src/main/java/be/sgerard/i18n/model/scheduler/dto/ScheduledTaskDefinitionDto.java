package be.sgerard.i18n.model.scheduler.dto;

import be.sgerard.i18n.model.core.localized.dto.LocalizedStringDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

import static be.sgerard.i18n.model.scheduler.dto.ScheduledTaskTriggerDto.toDto;

/**
 * Description of a scheduled task.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "ScheduledTaskDefinition", description = "Definition of a scheduled task.")
@JsonDeserialize(builder = ScheduledTaskDefinitionDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class ScheduledTaskDefinitionDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ScheduledTaskDefinitionEntity entity) {
        return builder()
                .id(entity.getId())
                .name(LocalizedStringDto.fromLocalizedString(entity.getName()))
                .description(LocalizedStringDto.fromLocalizedString(entity.getDescription()))
                .trigger(toDto(entity.getTrigger()))
                .enabled(entity.isEnabled())
                .lastExecutionTime(entity.getLastExecutionTime().orElse(null))
                .nextExecutionTime(entity.getNextExecutionTime().orElse(null));
    }

    @Schema(description = "Unique id of this kind of persistent task.", required = true)
    private final String id;

    @Schema(description = "Name of this kind of task (to be displayed to the end-user).", required = true)
    private final LocalizedStringDto name;

    @Schema(description = "Description of this kind of task (to be displayed to the end-user).", required = true)
    private final LocalizedStringDto description;

    @Schema(description = "Trigger definition: when the task must be executed.", required = true)
    private final ScheduledTaskTriggerDto trigger;

    @Schema(description = "Flag indicating whether the specified definition is enabled and can be executed.", required = true)
    private final boolean enabled;

    @Schema(description = "Last time the associated task was executed.")
    private final Instant lastExecutionTime;

    @Schema(description = "The date when the next execution will occur.")
    private final Instant nextExecutionTime;

    /**
     * @see #lastExecutionTime
     */
    public Optional<Instant> getLastExecutionTime() {
        return Optional.ofNullable(lastExecutionTime);
    }

    /**
     * @see #nextExecutionTime
     */
    public Optional<Instant> getNextExecutionTime() {
        return Optional.ofNullable(nextExecutionTime);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
