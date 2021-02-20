package be.sgerard.i18n.model.scheduler.snapshot;

import be.sgerard.i18n.model.core.localized.dto.LocalizedStringDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

/**
 * Dto for storing a {@link ScheduledTaskDefinitionEntity scheduled task definition} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = ScheduledTaskDefinitionSnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class ScheduledTaskDefinitionSnapshotDto {

    /**
     * @see ScheduledTaskDefinitionEntity#getId()
     */
    private final String id;

    /**
     * @see ScheduledTaskDefinitionEntity#getInternalId()
     */
    private final String internalId;

    /**
     * @see ScheduledTaskDefinitionEntity#getName()
     */
    private final LocalizedStringDto name;

    /**
     * @see ScheduledTaskDefinitionEntity#getDescription()
     */
    private final LocalizedStringDto description;

    /**
     * @see ScheduledTaskDefinitionEntity#getTrigger()
     */
    private final ScheduledTaskTriggerSnapshotDto trigger;

    /**
     * @see ScheduledTaskDefinitionEntity#isEnabled()
     */
    private final boolean enabled;

    /**
     * @see ScheduledTaskDefinitionEntity#getLastExecutionTime()
     */
    private final Instant lastExecutionTime;

    /**
     * @see #lastExecutionTime
     */
    public Optional<Instant> getLastExecutionTime() {
        return Optional.ofNullable(lastExecutionTime);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
