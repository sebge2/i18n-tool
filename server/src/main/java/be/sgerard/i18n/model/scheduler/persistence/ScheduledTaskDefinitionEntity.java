package be.sgerard.i18n.model.scheduler.persistence;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskTriggerEntity.fromDefinition;

/**
 * Definition of a scheduled task.
 *
 * @author Sebastien Gerard
 */
@Document("scheduled_task_definition")
@Getter
@Setter
@Accessors(chain = true)
@ToString(of = {"internalId"})
public class ScheduledTaskDefinitionEntity {

    /**
     * Unique id of this kind of persistent task.
     */
    @Id
    private String id;

    /**
     * Unique id of this kind of task. This is used internally.
     *
     * @see ScheduledTaskDefinition#getId()
     */
    @Indexed
    @NotNull
    private String internalId;

    /**
     * Name of this kind of task (to be displayed to the end-user).
     */
    @NotNull
    private LocalizedString name;

    /**
     * Description of this kind of task (to be displayed to the end-user).
     */
    @NotNull
    private LocalizedString description;

    /**
     * {@link ScheduledTaskTriggerEntity Trigger} definition: when the task must be executed.
     */
    @NotNull
    private ScheduledTaskTriggerEntity trigger;

    /**
     * Flag indicating whether the specified definition is enable and can be executed.
     */
    private boolean enabled;

    /**
     * Last time the associated task was executed.
     */
    private Instant lastExecutionTime;

    @PersistenceConstructor
    public ScheduledTaskDefinitionEntity() {
    }

    public ScheduledTaskDefinitionEntity(ScheduledTaskDefinition taskDefinition) {
        this.id = UUID.randomUUID().toString();
        this.internalId = taskDefinition.getId();
        this.name = taskDefinition.getName();
        this.description = taskDefinition.getDescription();
        this.trigger = fromDefinition(taskDefinition);
        this.enabled = true;
    }

    /**
     * @see #lastExecutionTime
     */
    public Optional<Instant> getLastExecutionTime() {
        return Optional.ofNullable(lastExecutionTime);
    }

    /**
     * Returns the date when the next execution will occur.
     */
    public Optional<Instant> getNextExecutionTime() {
        return Optional.of(getTrigger())
                .filter(trigger -> isEnabled())
                .flatMap(trigger -> trigger.getNextExecutionTime(getLastExecutionTime().orElse(null)));
    }

    /**
     * Returns whether the task has been completed based on the previous execution and the current trigger configuration.
     */
    public boolean isCompleted(){
        return getNextExecutionTime().isEmpty();
    }

    /**
     * @see #isEnabled()
     */
    public boolean isDisabled() {
        return !isEnabled();
    }

    /**
     * Returns the original {@link ScheduledTaskDefinition definition} of this entity.
     */
    public ScheduledTaskDefinition toDefinition() {
        return ScheduledTaskDefinition.builder()
                .id(getInternalId())
                .name(getName())
                .description(getDescription())
                .trigger(getTrigger().toDefinition())
                .build();
    }

}
