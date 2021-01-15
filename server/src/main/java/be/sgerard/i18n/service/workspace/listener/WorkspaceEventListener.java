package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.workspace.WorkspaceDtoEnricher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.event.EventType.*;

/**
 * {@link WorkspaceListener Workspace listener} emitting events when the workspace changes.
 *
 * @author Sebastien Gerard
 */
@Component
@Order
public class WorkspaceEventListener implements WorkspaceListener {

    private final EventService eventService;
    private final WorkspaceDtoEnricher dtoEnricher;

    public WorkspaceEventListener(EventService eventService, WorkspaceDtoEnricher dtoEnricher) {
        this.eventService = eventService;
        this.dtoEnricher = dtoEnricher;
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }

    @Override
    public Mono<Void> afterPersist(WorkspaceEntity workspace) {
        return dtoEnricher.mapAndEnrich(workspace)
                .flatMap(workspaceDto -> eventService.broadcastEvent(ADDED_WORKSPACE, workspaceDto));
    }

    @Override
    public Mono<Void> afterDelete(WorkspaceEntity workspace) {
        return dtoEnricher.mapAndEnrich(workspace)
                .flatMap(workspaceDto -> eventService.broadcastEvent(DELETED_WORKSPACE, workspaceDto));
    }

    @Override
    public Mono<Void> afterUpdate(WorkspaceEntity workspace) {
        return dtoEnricher.mapAndEnrich(workspace)
                .flatMap(workspaceDto -> eventService.broadcastEvent(UPDATED_WORKSPACE, workspaceDto));
    }
}
