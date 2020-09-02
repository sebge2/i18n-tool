package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.workspace.WorkspaceDtoEnricher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.event.EventType.*;

/**
 * {@link WorkspaceListener Workspace listener} emitting events when the workspace changes.
 *
 * @author Sebastien Gerard
 */
@Component
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
    public Mono<Void> onCreate(WorkspaceEntity workspace) {
        return eventService.broadcastEvent(ADDED_WORKSPACE, dtoEnricher.mapAndEnrich(workspace));
    }

    @Override
    public Mono<Void> onInitialize(WorkspaceEntity workspace) {
        return eventService.broadcastEvent(UPDATED_WORKSPACE, dtoEnricher.mapAndEnrich(workspace));
    }

    @Override
    public Mono<Void> onDelete(WorkspaceEntity workspace) {
        return eventService.broadcastEvent(DELETED_WORKSPACE, dtoEnricher.mapAndEnrich(workspace));
    }

    @Override
    public Mono<Void> onReview(WorkspaceEntity workspace) {
        return eventService.broadcastEvent(UPDATED_WORKSPACE, dtoEnricher.mapAndEnrich(workspace));
    }
}
