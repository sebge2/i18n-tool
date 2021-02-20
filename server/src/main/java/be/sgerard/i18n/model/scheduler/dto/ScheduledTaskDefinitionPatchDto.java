package be.sgerard.i18n.model.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

/**
 * Patch of a {@link ScheduledTaskDefinitionDto scheduled task definition}.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "ScheduledTaskDefinitionPatch", description = "Patch of a definition of a scheduled task.")
@JsonDeserialize(builder = ScheduledTaskDefinitionPatchDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class ScheduledTaskDefinitionPatchDto {

    @Schema(description = "Unique id of this kind of persistent task.", required = true)
    private final String id;

    @Schema(description = "Trigger definition: when the task must be executed.")
    private final ScheduledTaskTriggerDto trigger;

    /**
     * @see #trigger
     */
    public Optional<ScheduledTaskTriggerDto> getTrigger() {
        return Optional.ofNullable(trigger);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
