package be.sgerard.i18n.model.scheduler.persistence;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Entity keeping track of the execution of a scheduled task.
 *
 * @author Sebastien Gerard
 */
@Document("scheduled_task_execution")
@Getter
@Setter
@Accessors(chain = true)
public class ScheduledTaskExecutionEntity {

    /**
     * The unique id of this execution.
     */
    @Id
    private String id;

    /**
     * The associated {@link ScheduledTaskDefinitionEntity task definition}.
     */
    @NotNull
    private String definitionId;

    /**
     * Time when the execution started.
     */
    @NotNull
    private Instant startTime;

    /**
     * Time when the execution ended.
     */
    @NotNull
    private Instant endTime;

    /**
     * Status at the end of the execution.
     */
    @NotNull
    private ScheduledTaskExecutionStatus status;

    /**
     * Short description (characters are truncated if there is not enough place on the screen) of the result to be displayed to the end-user.
     * The description may contain HTML.
     */
    @NotNull
    private LocalizedString shortDescription;

    /**
     * Description of the result to be displayed to the end-user. If no description is provided, the short description is used. The description may contain HTML.
     */
    private LocalizedString description;

    @PersistenceConstructor
    ScheduledTaskExecutionEntity() {
    }

    public ScheduledTaskExecutionEntity(ScheduledTaskDefinitionEntity taskDefinition) {
        this.id = UUID.randomUUID().toString();

        this.definitionId = taskDefinition.getId();
    }

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
