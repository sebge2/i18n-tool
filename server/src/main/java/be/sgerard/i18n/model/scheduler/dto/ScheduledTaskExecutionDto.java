package be.sgerard.i18n.model.scheduler.dto;

import be.sgerard.i18n.model.core.localized.dto.LocalizedStringDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Execution of a scheduled task.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "ScheduledTaskExecution", description = "Execution of a scheduled task.")
@JsonDeserialize(builder = ScheduledTaskExecutionDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class ScheduledTaskExecutionDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ScheduledTaskExecutionEntity entity) {
        return builder()
                .id(entity.getId())
                .definitionId(entity.getDefinitionId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .shortDescription(LocalizedStringDto.fromLocalizedString(entity.getShortDescription()))
                .description(entity.getDescription().map(LocalizedStringDto::fromLocalizedString).orElse(null))
                .durationInMs(entity.getDuration().toMillis());
    }

    @Schema(description = "The unique id of this execution.", required = true)
    private final String id;

    @Schema(description = "The associated task definition", required = true)
    private final String definitionId;

    @Schema(description = "Time when the execution started.", required = true)
    private final Instant startTime;

    @Schema(description = "Time when the execution ended.", required = true)
    private final Instant endTime;

    @Schema(description = "Status at the end of the execution.", required = true)
    private final ScheduledTaskExecutionStatus status;

    @Schema(description = "Short description (characters are truncated if there is not enough place on the screen) of the result to be displayed to the end-user. The description may contain HTML.", required = true)
    private final LocalizedStringDto shortDescription;

    @Schema(description = "Description of the result to be displayed to the end-user. If no description is provided, the short description is used. The description may contain HTML.")
    private final LocalizedStringDto description;

    @Schema(description = "The duration in milli-seconds of this execution.", required = true)
    private final long durationInMs;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
