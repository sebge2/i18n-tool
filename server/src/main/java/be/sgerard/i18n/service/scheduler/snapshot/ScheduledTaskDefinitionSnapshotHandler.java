package be.sgerard.i18n.service.scheduler.snapshot;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.core.localized.dto.LocalizedStringDto;
import be.sgerard.i18n.model.scheduler.persistence.NonRecurringScheduledTaskTriggerEntity;
import be.sgerard.i18n.model.scheduler.persistence.RecurringScheduledTaskTriggerEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskTriggerEntity;
import be.sgerard.i18n.model.scheduler.snapshot.NonRecurringScheduledTaskTriggerSnapshotDto;
import be.sgerard.i18n.model.scheduler.snapshot.RecurringScheduledTaskTriggerSnapshotDto;
import be.sgerard.i18n.model.scheduler.snapshot.ScheduledTaskDefinitionSnapshotDto;
import be.sgerard.i18n.model.scheduler.snapshot.ScheduledTaskTriggerSnapshotDto;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.scheduler.ScheduledTaskDefinitionRepository;
import be.sgerard.i18n.service.scheduler.ScheduledTaskManager;
import be.sgerard.i18n.service.scheduler.validation.ScheduledTaskDefinitionValidator;
import be.sgerard.i18n.service.snapshot.BaseSnapshotHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link BaseSnapshotHandler Snapshot handler} for {@link ScheduledTaskDefinitionEntity scheduled task definitions}.
 *
 * @author Sebastien Gerard
 */
@Component
public class ScheduledTaskDefinitionSnapshotHandler extends BaseSnapshotHandler<ScheduledTaskDefinitionEntity, ScheduledTaskDefinitionSnapshotDto> {

    /**
     * Name of the file containing definitions.
     */
    public static final String FILE = "scheduled_task_definition.json";

    private final ScheduledTaskDefinitionValidator validator;
    private final ScheduledTaskManager taskManager;

    public ScheduledTaskDefinitionSnapshotHandler(ObjectMapper objectMapper,
                                                  ScheduledTaskDefinitionRepository repository,
                                                  ScheduledTaskDefinitionValidator validator,
                                                  ScheduledTaskManager taskManager) {
        super(FILE, ScheduledTaskDefinitionSnapshotDto.class, objectMapper, repository);

        this.validator = validator;
        this.taskManager = taskManager;
    }

    @Override
    public int getImportPriority() {
        return 60;
    }

    @Override
    protected Mono<ValidationResult> validate(ScheduledTaskDefinitionEntity taskDefinition) {
        return validator.beforeCreateOrUpdate(taskDefinition);
    }

    @Override
    protected Mono<ScheduledTaskDefinitionEntity> afterSave(ScheduledTaskDefinitionEntity entity) {
        return taskManager.createOrUpdate(entity.toDefinition());
    }

    @Override
    protected Mono<ScheduledTaskDefinitionEntity> mapFromDto(ScheduledTaskDefinitionSnapshotDto dto) {
        return Mono.just(
                new ScheduledTaskDefinitionEntity()
                        .setId(dto.getId())
                        .setInternalId(dto.getInternalId())
                        .setName(LocalizedString.fromDto(dto.getName()))
                        .setDescription(LocalizedString.fromDto(dto.getDescription()))
                        .setEnabled(dto.isEnabled())
                        .setLastExecutionTime(dto.getLastExecutionTime().orElse(null))
                        .setTrigger(mapFromDto(dto.getTrigger()))
        );
    }

    @Override
    protected Mono<ScheduledTaskDefinitionSnapshotDto> mapToDto(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono.just(
                ScheduledTaskDefinitionSnapshotDto.builder()
                        .id(taskDefinition.getId())
                        .internalId(taskDefinition.getInternalId())
                        .name(LocalizedStringDto.fromLocalizedString(taskDefinition.getName()))
                        .description(LocalizedStringDto.fromLocalizedString(taskDefinition.getDescription()))
                        .enabled(taskDefinition.isEnabled())
                        .lastExecutionTime(taskDefinition.getLastExecutionTime().orElse(null))
                        .trigger(mapToDto(taskDefinition.getTrigger()))
                        .build()
        );
    }

    /**
     * Maps the specified trigger to its snapshot representation.
     */
    private ScheduledTaskTriggerSnapshotDto mapToDto(ScheduledTaskTriggerEntity trigger) {
        switch (trigger.getType()) {
            case RECURRING:
                final RecurringScheduledTaskTriggerEntity recurringTrigger = (RecurringScheduledTaskTriggerEntity) trigger;

                return new RecurringScheduledTaskTriggerSnapshotDto(recurringTrigger.getCronExpression());
            case NON_RECURRING:
                final NonRecurringScheduledTaskTriggerEntity nonRecurringTrigger = (NonRecurringScheduledTaskTriggerEntity) trigger;

                return new NonRecurringScheduledTaskTriggerSnapshotDto(nonRecurringTrigger.getStartTime());
            default:
                throw new UnsupportedOperationException(String.format("Unsupported type [%s].", trigger.getType()));
        }
    }

    /**
     * Maps the specified trigger from its snapshot representation.
     */
    private ScheduledTaskTriggerEntity mapFromDto(ScheduledTaskTriggerSnapshotDto trigger) {
        switch (trigger.getType()) {
            case RECURRING:
                final RecurringScheduledTaskTriggerSnapshotDto recurringTrigger = (RecurringScheduledTaskTriggerSnapshotDto) trigger;

                return new RecurringScheduledTaskTriggerEntity(recurringTrigger.getCronExpression());
            case NON_RECURRING:
                final NonRecurringScheduledTaskTriggerSnapshotDto nonRecurringTrigger = (NonRecurringScheduledTaskTriggerSnapshotDto) trigger;

                return new NonRecurringScheduledTaskTriggerEntity(nonRecurringTrigger.getStartTime());
            default:
                throw new UnsupportedOperationException(String.format("Unsupported type [%s].", trigger.getType()));
        }
    }
}
