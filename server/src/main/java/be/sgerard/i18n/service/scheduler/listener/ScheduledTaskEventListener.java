package be.sgerard.i18n.service.scheduler.listener;

import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskExecutionDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.event.EventType.*;

/**
 * {@link ScheduledTaskListener Listener} of scheduled tasks that emits events accordingly.
 *
 * @author Sebastien Gerard
 */
@Component
public class ScheduledTaskEventListener implements ScheduledTaskListener {

    private final EventService eventService;

    public ScheduledTaskEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Mono<Void> afterPersist(ScheduledTaskDefinitionEntity taskDefinition) {
        return eventService.sendEventToUsers(UserRole.ADMIN, ADDED_SCHEDULED_TASK_DEFINITION, ScheduledTaskDefinitionDto.builder(taskDefinition).build());
    }

    @Override
    public Mono<Void> afterUpdate(ScheduledTaskDefinitionEntity taskDefinition) {
        return eventService.sendEventToUsers(UserRole.ADMIN, UPDATED_SCHEDULED_TASK_DEFINITION, ScheduledTaskDefinitionDto.builder(taskDefinition).build());
    }

    @Override
    public Mono<Void> afterDelete(ScheduledTaskDefinitionEntity taskDefinition) {
        return eventService.sendEventToUsers(UserRole.ADMIN, DELETED_SCHEDULED_TASK_DEFINITION, ScheduledTaskDefinitionDto.builder(taskDefinition).build());
    }

    @Override
    public Mono<Void> afterExecute(ScheduledTaskExecutionEntity execution) {
        return eventService.sendEventToUsers(UserRole.ADMIN, ADDED_SCHEDULED_TASK_EXECUTION, ScheduledTaskExecutionDto.builder(execution).build());
    }

    @Override
    public Mono<Void> afterDelete(ScheduledTaskExecutionEntity execution) {
        return eventService.sendEventToUsers(UserRole.ADMIN, DELETED_SCHEDULED_TASK_EXECUTION, ScheduledTaskExecutionDto.builder(execution).build());
    }
}
