package be.sgerard.i18n.model.scheduler;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Execution of a scheduled task that is now ended.
 *
 * @author Sebastien Gerard
 */
@Getter
@Builder(builderClassName = "Builder")
public class ScheduledTaskExecution {

    /**
     * The associated {@link ScheduledTaskDefinitionEntity task definition}.
     */
    private final ScheduledTaskDefinitionEntity taskDefinition;

    /**
     * Time when the execution started.
     */
    private final Instant startTime;

    /**
     * Time when the execution ended.
     */
    private final Instant endTime;

    /**
     * Status at the end of the execution.
     */
    private final ScheduledTaskExecutionStatus status;

    /**
     * Description of the result to be displayed to the end-user.
     */
    private final LocalizedString shortDescription;

    /**
     * Description of the result to be displayed to the end-user.
     */
    private final LocalizedString description;

    /**
     * @see #description
     */
    public Optional<LocalizedString> getDescription() {
        return Optional.ofNullable(description);
    }

    /**
     * Returns the {@link Duration duration} of this execution.
     */
    public Duration getDuration() {
        return Duration.between(getStartTime(), getEndTime());
    }

}
